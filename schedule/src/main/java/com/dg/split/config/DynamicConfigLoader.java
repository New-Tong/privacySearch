package com.dg.split.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePropertySource;


import java.io.IOException;

@Configuration
public class DynamicConfigLoader {

    private final ConfigurableEnvironment environment;
    private final ResourceLoader resourceLoader;

    @Autowired
    public DynamicConfigLoader(ConfigurableEnvironment environment, ResourceLoader resourceLoader) {
        this.environment = environment;
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void loadExternalConfig() throws IOException {
        String configPath = System.getProperty("config.path");
        if (configPath != null) {
            System.out.println("Loading external config from: " + configPath);
            PropertySource<?> propertySource = new ResourcePropertySource("externalProperties", "file:" + configPath);
            environment.getPropertySources().addLast(propertySource);
            System.out.println("External config loaded successfully.");
        } else {
            System.out.println("No external config path specified.");
        }
    }


//    @PostConstruct
//    public void loadExternalConfig() throws IOException {
//        // 从 JVM 参数中获取 config.path
//        String configPath = System.getProperty("config.path");
//
//        if (configPath != null) {
//            // 动态加载外部配置文件
//            PropertySource<?> propertySource = new ResourcePropertySource("externalProperties", "file:" + configPath);
//            environment.getPropertySources().addLast(propertySource);
//            System.out.println("Loaded external config from: " + configPath);
//        } else {
//            System.out.println("No external config path specified, skipping external config loading.");
//        }
//    }
}
