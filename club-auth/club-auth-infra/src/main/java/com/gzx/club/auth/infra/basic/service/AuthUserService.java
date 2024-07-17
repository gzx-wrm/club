package com.gzx.club.auth.infra.basic.service;

import com.gzx.club.auth.infra.basic.entity.AuthUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * 用户信息表(AuthUser)表服务接口
 *
 * @author makejava
 * @since 2024-06-24 18:25:26
 */
public interface AuthUserService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    AuthUser queryById(Long id);


    /**
     * 新增数据
     *
     * @param authUser 实例对象
     * @return 实例对象
     */
    Integer insert(AuthUser authUser);

    /**
     * 修改数据
     *
     * @param authUser 实例对象
     * @return 实例对象
     */
    Integer update(AuthUser authUser);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

    List<AuthUser> queryByCondition(AuthUser existAuthUser);

    Integer updateByUsername(AuthUser authUser);

    List<AuthUser> queryByIds(List<String> userNameList);
}
