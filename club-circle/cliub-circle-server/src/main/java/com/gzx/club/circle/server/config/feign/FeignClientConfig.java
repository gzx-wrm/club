package com.gzx.club.circle.server.config.feign;

import com.gzx.club.circle.server.interceptor.FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: club
 * @description: 配置feign的拦截器
 * @author: gzx
 * @create: 2024-06-29 14:02
 **/
@Configuration
public class FeignClientConfig {

    @Bean
    public FeignRequestInterceptor feignRequestInterceptor() {
        return new FeignRequestInterceptor();
    }
}
