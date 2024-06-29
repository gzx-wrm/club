package com.gzx.club.subject.application.controller;

import com.gzx.club.subject.infra.entity.UserInfo;
import com.gzx.club.subject.infra.rpc.UserRPC;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @program: club
 * @description: 测试
 * @author: gzx
 * @create: 2024-06-29 13:39
 **/
@RestController
public class TestController {

    @Resource
    private UserRPC userRPC;

    @RequestMapping("/test")
    public UserInfo getUserInfo() {
        return userRPC.getUserInfo("gzx");
    }
}
