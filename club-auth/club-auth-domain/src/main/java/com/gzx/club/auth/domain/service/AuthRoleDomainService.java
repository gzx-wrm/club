package com.gzx.club.auth.domain.service;

import com.gzx.club.auth.domain.entity.AuthRoleBO;

public interface AuthRoleDomainService {
    Boolean add(AuthRoleBO authRoleBO);

    Boolean update(AuthRoleBO authRoleBO);

    Boolean delete(AuthRoleBO authRoleBO);
}
