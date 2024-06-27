package com.gzx.club.auth.domain.service.impl;

import com.gzx.club.auth.common.enums.IsDeletedFlagEnum;
import com.gzx.club.auth.domain.convert.AuthRoleBOConverter;
import com.gzx.club.auth.domain.entity.AuthRoleBO;
import com.gzx.club.auth.domain.service.AuthRoleDomainService;
import com.gzx.club.auth.infra.basic.entity.AuthRole;
import com.gzx.club.auth.infra.basic.service.AuthRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @program: club
 * @description: 角色服务实现类
 * @author: gzx
 * @create: 2024-06-24 19:22
 **/
@Service
public class AuthRoleDomainServiceImpl implements AuthRoleDomainService {

    @Resource
    AuthRoleService authRoleService;

    @Override
    public Boolean add(AuthRoleBO authRoleBO) {
        AuthRole authRole = AuthRoleBOConverter.INSTANCE.convertBOToEntity(authRoleBO);
        authRole.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        Integer count = authRoleService.insert(authRole);
        return count > 0;
    }

    @Override
    public Boolean update(AuthRoleBO authRoleBO) {
        AuthRole authRole = AuthRoleBOConverter.INSTANCE.convertBOToEntity(authRoleBO);
        Integer count = authRoleService.update(authRole);
        return count > 0;
    }

    @Override
    public Boolean delete(AuthRoleBO authRoleBO) {
        AuthRole authRole = new AuthRole();
        authRole.setId(authRoleBO.getId());
        authRole.setIsDeleted(IsDeletedFlagEnum.DELETED.getCode());
        Integer count = authRoleService.update(authRole);
        return count > 0;
    }
}
