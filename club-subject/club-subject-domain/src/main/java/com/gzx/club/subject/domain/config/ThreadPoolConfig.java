package com.gzx.club.subject.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @program: club
 * @description: 线程池配置
 * @author: gzx
 * @create: 2024-06-27 20:17
 **/
@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor labelThreadPool() {
        // 配置线程池的具体参数，核心线程数为20，最大线程数为100，线程存活时间为5s，等待队列，线程工厂，拒绝策略为调用者执行
        return new ThreadPoolExecutor(20,
                100,
                5,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
