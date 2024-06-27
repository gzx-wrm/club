package com.gzx.club.auth.infra.basic.service.impl;

import com.gzx.club.auth.infra.basic.entity.AuthRolePermission;
import com.gzx.club.auth.infra.basic.mapper.AuthRolePermissionDao;
import com.gzx.club.auth.infra.basic.service.AuthRolePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.util.List;

/**
 * 角色权限关联表(AuthRolePermission)表服务实现类
 *
 * @author makejava
 * @since 2024-06-24 18:25:26
 */
@Service("authRolePermissionService")
public class AuthRolePermissionServiceImpl implements AuthRolePermissionService {
    @Resource
    private AuthRolePermissionDao authRolePermissionDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public AuthRolePermission queryById(Long id) {
        return this.authRolePermissionDao.queryById(id);
    }


    /**
     * 新增数据
     *
     * @param authRolePermission 实例对象
     * @return
     */
    @Override
    public Integer insert(AuthRolePermission authRolePermission) {
        return this.authRolePermissionDao.insert(authRolePermission);
    }

    /**
     * 修改数据
     *
     * @param authRolePermission 实例对象
     * @return
     */
    @Override
    public Integer update(AuthRolePermission authRolePermission) {
        return this.authRolePermissionDao.update(authRolePermission);
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.authRolePermissionDao.deleteById(id) > 0;
    }

    @Override
    public List<AuthRolePermission> queryByCondition(AuthRolePermission authRolePermission) {
        return this.authRolePermissionDao.queryByCondition(authRolePermission);
    }

    @Override
    public Integer insertBatch(List<AuthRolePermission> rolePermissionList) {
        return this.authRolePermissionDao.insertBatch(rolePermissionList);
    }
}
