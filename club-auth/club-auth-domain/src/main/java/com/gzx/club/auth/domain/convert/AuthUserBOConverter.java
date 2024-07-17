package com.gzx.club.auth.domain.convert;

import com.gzx.club.auth.domain.entity.AuthUserBO;
import com.gzx.club.auth.infra.basic.entity.AuthUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户bo转换器
 *
 */
@Mapper
public interface AuthUserBOConverter {

    AuthUserBOConverter INSTANCE = Mappers.getMapper(AuthUserBOConverter.class);

    AuthUser convertBOToEntity(AuthUserBO authUserBO);

    AuthUserBO convertEntityToBO(AuthUser user);

    List<AuthUserBO> convertEntityToBO(List<AuthUser> user);
}
