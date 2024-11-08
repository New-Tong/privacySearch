package com.dg.split;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.dg.split.dao")
@ComponentScan(basePackages = {"com.github.tobato","com.dg.split.controller","com.dg.split"})
@EnableFeignClients(basePackages = "com.dg.split.feign")
public class SplitApplication {
    public static void main(String[] args) {
        SpringApplication.run(SplitApplication.class, args);
    }
}
