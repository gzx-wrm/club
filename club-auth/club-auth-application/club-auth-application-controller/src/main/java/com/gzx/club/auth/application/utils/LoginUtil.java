package com.gzx.club.auth.application.utils;


import com.gzx.club.auth.application.context.ContextHolder;

/**
 * @program: club
 * @description: 登录工具类
 * @author: gzx
 * @create: 2024-06-28 19:32
 **/
public class LoginUtil {

    public static String getLoginId() {
        return ContextHolder.getLoginId();
    }
}
