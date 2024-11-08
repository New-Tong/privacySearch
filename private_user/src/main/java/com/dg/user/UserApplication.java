package com.dg.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @version v1.0.0
 * @belongsProject: private_search
 * @belongsPackage: com.dg.user
 * @author: XBin
 * @description: 启动类
 * @createTime: 2023-11-13 15:34
 */

@SpringBootApplication
//@EnableDiscoveryClient
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}