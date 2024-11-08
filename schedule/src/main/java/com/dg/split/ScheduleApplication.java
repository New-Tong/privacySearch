package com.dg.split;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


//定时获取数据库的文档
@SpringBootApplication
@EnableScheduling
public class ScheduleApplication {


    public static void main(String[] args) {
        SpringApplication.run(ScheduleApplication.class, args);
    }
}
