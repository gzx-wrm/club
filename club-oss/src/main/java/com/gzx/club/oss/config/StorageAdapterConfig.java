package com.gzx.club.oss.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.gzx.club.oss.annotations.StorageAdapter;
import lombok.Data;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * @program: club
 * @description: 文件存储服务适配器配置类
 * @author: gzx
 * @create: 2024-06-19 23:54
 **/
@Configuration
@RefreshScope
public class StorageAdapterConfig {

    @Value("${storage.adapter}")
    private String adapterName;

    @Bean
    @RefreshScope
    public com.gzx.club.oss.adapter.StorageAdapter storageAdapter() {
        Reflections reflections = new Reflections("com.gzx.club.oss.adapter");
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(StorageAdapter.class);

        for (Class<?> clazz : annotatedClasses) {
            StorageAdapter annotation = clazz.getAnnotation(StorageAdapter.class);
            if (annotation.value().equals(adapterName)) {
                if (com.gzx.club.oss.adapter.StorageAdapter.class.isAssignableFrom(clazz)) {
                    try {
                        return (com.gzx.club.oss.adapter.StorageAdapter) clazz.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create instance of " + clazz.getName(), e);
                    }
                }
                else {
                    throw new IllegalArgumentException("Class " + clazz.getName() + " does not implement IStorageAdapter");
                }
            }
        }

        throw new IllegalArgumentException("No adapter found with name: " + adapterName);
    }
}
