package com.healthcare.catalog.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ConnectionPoolSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoPoolConfig {

    @Bean
    public MongoClient mongoClient() {
        ConnectionPoolSettings poolSettings = ConnectionPoolSettings.builder()
                .maxSize(150)
                .minSize(20)
                .build();

        return MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                .applyToConnectionPoolSettings(builder -> builder.applySettings(poolSettings))
                .build());
    }
}