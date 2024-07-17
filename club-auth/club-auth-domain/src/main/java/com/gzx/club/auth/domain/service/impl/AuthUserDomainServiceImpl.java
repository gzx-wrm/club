package com.gzx.club.auth.domain.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.gson.Gson;
import com.gzx.club.auth.common.enums.AuthUserStatusEnum;
import com.gzx.club.auth.common.enums.IsDeletedFlagEnum;
import com.gzx.club.auth.domain.constants.AuthConstant;
import com.gzx.club.auth.domain.convert.AuthUserBOConverter;
import com.gzx.club.auth.domain.entity.AuthUserBO;
import com.gzx.club.auth.domain.redis.RedisUtil;
import com.gzx.club.auth.domain.service.AuthUserDomainService;
import com.gzx.club.auth.infra.basic.entity.*;
import com.gzx.club.auth.infra.basic.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: club
 * @description: 用户模块服务实现类
 * @author: gzx
 * @create: 2024-06-24 19:34
 **/
@Service
public class AuthUserDomainServiceImpl implements AuthUserDomainService {

    @Resource
    AuthUserService authUserService;

    @Resource
    AuthRoleService authRoleService;

    @Resource
    AuthUserRoleService authUserRoleService;

    @Resource
    AuthPermissionService authPermissionService;

    @Resource
    AuthRolePermissionService authRolePermissionService;

    private static final String md5Salt = "clubclubclub";

    @Resource
    private RedisUtil redisUtil;

    private static String authPermissionPrefix = "auth.permission";

    private static String authRolePrefix = "auth.role";

    private static final String LOGIN_PREFIX = "loginCode";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean register(AuthUserBO authUserBO) {
        AuthUser existAuthUser = new AuthUser();
        existAuthUser.setUserName(authUserBO.getUserName());
        List<AuthUser> existUserList = authUserService.queryByCondition(existAuthUser);
        if (existUserList.size() > 0) {
            return true;
        }
        AuthUser authUser = AuthUserBOConverter.INSTANCE.convertBOToEntity(authUserBO);
        authUser.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.code);
        authUser.setPassword(SaSecureUtil.md5BySalt(authUser.getPassword(), md5Salt));
        if (StringUtils.isBlank(authUser.getAvatar())) {
            authUser.setAvatar("http://117.72.10.84:9000/user/icon/微信图片_20231203153718(1).png");
        }
        authUser.setStatus(AuthUserStatusEnum.OPEN.getCode());
        Integer count = authUserService.insert(authUser);

        // 接下来更新userRole表
        AuthUserRole authUserRole = new AuthUserRole();
        authUserRole.setUserId(authUser.getId());
        AuthRole authRole = new AuthRole();
        authRole.setRoleKey(AuthConstant.NORMAL_USER);
        AuthRole authRole1 = authRoleService.queryByCondition(authRole).get(0);
        authUserRole.setRoleId(authRole1.getId());
        authUserRole.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.code);
        Integer count1 = authUserRoleService.insert(authUserRole);

        // 然后将权限写到redis中，登录要用
        String roleKey = redisUtil.buildKey(authRolePrefix, authUser.getUserName());
        List<AuthRole> roleList = new LinkedList<>();
        roleList.add(authRole1);
        redisUtil.set(roleKey, new Gson().toJson(roleList));

        AuthRolePermission authRolePermission = new AuthRolePermission();
        authRolePermission.setRoleId(authRole1.getId());
        List<AuthRolePermission> rolePermissionList = authRolePermissionService.
                queryByCondition(authRolePermission);

        List<Long> permissionIdList = rolePermissionList.stream()
                .map(AuthRolePermission::getPermissionId).collect(Collectors.toList());
        //根据roleId查权限
        List<AuthPermission> permissionList = authPermissionService.queryByIds(permissionIdList);
        String permissionKey = redisUtil.buildKey(authPermissionPrefix, authUser.getUserName());
        redisUtil.set(permissionKey, new Gson().toJson(permissionList));
        return count > 0 && count1 > 0;
    }

    @Override
    public Boolean update(AuthUserBO authUserBO) {
        AuthUser authUser = AuthUserBOConverter.INSTANCE.convertBOToEntity(authUserBO);
        Integer count = authUserService.updateByUsername(authUser);
        //有任何的更新，都要与缓存进行同步的修改
        return count > 0;
    }

    @Override
    public Boolean delete(AuthUserBO authUserBO) {
        AuthUser authUser = new AuthUser();
        authUser.setId(authUserBO.getId());
        authUser.setIsDeleted(IsDeletedFlagEnum.DELETED.getCode());
        Integer count = authUserService.update(authUser);
        //有任何的更新，都要与缓存进行同步的修改
        return count > 0;
    }

    @Override
    public SaTokenInfo login(String verifyCode) {
        String key = redisUtil.buildKey(LOGIN_PREFIX, verifyCode);
        String username = redisUtil.get(key);
        if (StringUtils.isBlank(username)) {
            return null;
        }
        AuthUserBO authUserBO = new AuthUserBO();
        authUserBO.setUserName(username);
        this.register(authUserBO);
        StpUtil.login(username);
        return StpUtil.getTokenInfo();
    }

    @Override
    public AuthUserBO getUserInfo(AuthUserBO authUserBO) {
        AuthUser authUser = new AuthUser();
        authUser.setUserName(authUserBO.getUserName());
        List<AuthUser> userList = authUserService.queryByCondition(authUser);
        if(CollectionUtils.isEmpty(userList)){
            return new AuthUserBO();
        }
        AuthUser user = userList.get(0);
        return AuthUserBOConverter.INSTANCE.convertEntityToBO(user);
    }

    @Override
    public List<AuthUserBO> listUserInfoByIds(List<String> userNameList) {
        List<AuthUser> userList = authUserService.queryByIds(userNameList);
        if (CollectionUtils.isEmpty(userList)) {
            return Collections.emptyList();
        }
        return AuthUserBOConverter.INSTANCE.convertEntityToBO(userList);
    }
}
