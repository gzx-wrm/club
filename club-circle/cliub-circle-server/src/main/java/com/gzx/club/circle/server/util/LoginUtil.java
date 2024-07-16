package com.gzx.club.circle.server.util;


import com.gzx.club.circle.server.context.ContextHolder;

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
