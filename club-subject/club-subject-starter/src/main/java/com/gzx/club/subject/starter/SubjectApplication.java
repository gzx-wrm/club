package com.gzx.club.subject.starter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.gzx.club.subject")
@MapperScan("com.gzx.club.subject.**.mapper")
public class SubjectApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(SubjectApplication.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
