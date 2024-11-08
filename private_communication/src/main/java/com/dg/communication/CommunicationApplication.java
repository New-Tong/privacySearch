package com.dg.communication;

import com.dg.communication.service.NettyTcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @version v1.0.0
 * @belongsProject: private_search
 * @belongsPackage: com.dg.communication
 * @author: XBin
 * @description: 与其他项目进行通信
 * @createTime: 2023-11-20 10:27
 */

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.dg.communication.feign")
@ComponentScan({"com.dg.communication.service","com.dg.communication.feign"})
public class CommunicationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunicationApplication.class, args);
    }

}