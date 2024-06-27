package com.gzx.club.auth.application.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * (AuthRolePermission)实体类
 *
 * @author makejava
 * @since 2023-11-04 22:16:00
 */
@Data
public class AuthRolePermissionDTO implements Serializable {
    
    private Long id;
    
    private Long roleId;
    
    private Long permissionId;

    private List<Long> permissionIdList;
}

