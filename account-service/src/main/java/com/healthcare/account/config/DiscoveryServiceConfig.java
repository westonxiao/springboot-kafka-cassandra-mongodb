package com.healthcare.account.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDiscoveryClient
public class DiscoveryServiceConfig {
    // Ribbon will be auto-configured
}