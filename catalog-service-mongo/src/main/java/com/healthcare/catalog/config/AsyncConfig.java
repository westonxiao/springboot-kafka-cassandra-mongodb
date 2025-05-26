package com.healthcare.catalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.healthcare.catalog.repository")
public class AsyncConfig {

    @Bean(name = "catalogTaskExecutor")
    public Executor taskExecutor() {
        return Executors.newFixedThreadPool(10); // Adjust pool size
    }
}