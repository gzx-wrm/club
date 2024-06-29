package com.gzx.club.subject.infra.rpc;

import com.gzx.club.auth.api.api.UserFeignService;
import com.gzx.club.auth.api.entity.AuthUserDTO;
import com.gzx.club.auth.api.entity.Result;
import com.gzx.club.subject.infra.entity.UserInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserRPC {

    @Resource
    private UserFeignService userFeignService;

    public UserInfo getUserInfo(String username) {
        AuthUserDTO authUserDTO = new AuthUserDTO();
        authUserDTO.setUserName(username);
        Result<AuthUserDTO> result = userFeignService.getUserInfo(authUserDTO);
        UserInfo userInfo = new UserInfo();
        if (!result.getSuccess()) {
            return userInfo;
        }
        userInfo.setUserName(result.getData().getUserName());
        userInfo.setNickName(result.getData().getNickName());

        return userInfo;
    }
}
