package com.gzx.club.circle.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 圈子微服务启动类
 */
@SpringBootApplication
@MapperScan("com.gzx.club.**.dao")
@EnableFeignClients(basePackages = "com.gzx")
public class CircleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CircleApplication.class);
    }

}
