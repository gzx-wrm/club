package com.gzx.club.auth.application.context;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: club
 * @description: 存储上下文信息的容器
 * @author: gzx
 * @create: 2024-06-28 19:22
 **/
public class ContextHolder {

    private static final InheritableThreadLocal<Map<String, Object>> CONTEXT = new InheritableThreadLocal<>();

    /**
     * @Author: gzx
     *
     * @Description: 向上下文容器中获取值，如果没有则返回null
     * @Date: 2024-06-28
     */
    public static Object get(String key) {
        Map<String, Object> contextMap = getContextMap();
        return contextMap.get(key);
    }

    /**
     * @Author: gzx
     *
     * @Description: 向上下文容器中设置值，并返回旧值
     * @Date: 2024-06-28
     */
    public static Object set(String key, Object value) {
        Map<String, Object> contextMap = getContextMap();
        if (Objects.nonNull(value)) {
            return contextMap.put(key, value);
        }
        return null;
    }

    public static void remove() {
        CONTEXT.remove();
    }

    public static String getLoginId() {
        Object loginId = get("loginId");
        if (Objects.isNull(loginId)) {
            return null;
        }
        return (String) loginId;
    }

    private static Map<String, Object> getContextMap() {
        Map<String, Object> contextMap = CONTEXT.get();
        if (Objects.isNull(contextMap)) {
            CONTEXT.set(new ConcurrentHashMap<>());
        }
        return CONTEXT.get();
    }
}
