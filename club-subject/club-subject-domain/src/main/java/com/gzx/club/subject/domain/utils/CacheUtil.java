package com.gzx.club.subject.domain.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @program: club
 * @description: guava本地缓存工具类
 * @author: gzx
 * @create: 2024-06-30 16:17
 **/
@Component
@Slf4j
public class CacheUtil {

    // 定义本地缓存
    private Cache<String, String> localCache = CacheBuilder.newBuilder().maximumSize(5000)
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build();

    // 泛型方法，用于获取不同类型的缓存
    public <T> List<T> getCache(String cacheKey, Class<T> clazz, Function<String, List<T>> fetchFunc) {
        List<T> result = new ArrayList<>();
        String content = localCache.getIfPresent(cacheKey);
        if (!StringUtils.isBlank(content)) {
            try {
                result = JSON.parseArray(content, clazz);
                return result;
            } catch (Exception e) {
                log.error("CacheUtil.getCache: {}", e.getMessage(), e);
                return result;
            }
        }

        // 如果获取不到就需要调用函数重新刷新缓存
        result = fetchFunc.apply(cacheKey);
        if (!CollectionUtils.isEmpty(result)) {
            localCache.put(cacheKey, JSON.toJSONString(result));
        }
        return result;
    }
}
