package com.gzx.club.practice.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gzx.club.practice.**.mapper")
public class PracticeApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(PracticeApplication.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
