package com.dg.query;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 文件搜索模块  从index搜索出索引，然后去其他的数据库查找出内容解密拼接
 */
@SpringBootApplication
@MapperScan("com.dg.query.dao")
@EnableDiscoveryClient
@EnableFeignClients
public class QueryApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueryApplication.class, args);
    }

}

