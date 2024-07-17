package com.gzx.club.auth.api.api;

import com.gzx.club.auth.api.entity.AuthUserDTO;
import com.gzx.club.auth.api.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author: gzx
 *
 * @Description: 提供给外界的用户微服务，声明式的客户端
 * @Date: 2024-06-29
 */
@FeignClient("club-auth")
public interface UserFeignService {

    @RequestMapping("/user/getUserInfo")
    Result<AuthUserDTO> getUserInfo(@RequestBody AuthUserDTO authUserDTO);

    @RequestMapping("/user/listByIds")
    Result<List<AuthUserDTO>> listUserInfoByIds(@RequestBody List<String> userNameList);
}
