package com.healthcare.gateway.config;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.*;

@Configuration
public class LoadBalancerConfig {

    @Bean
    public ServiceInstanceListSupplier serviceInstanceListSupplier() {
        return new ServiceInstanceListSupplier() {
            @Override
            public String getServiceId() {
                return "api-gateway";
            }

            @Override
            public Flux<List<ServiceInstance>> get() {
                return Flux.just(Arrays.asList(
                        new DefaultServiceInstance("gateway-1", "api-gateway", "localhost", 8080, false),
                        new DefaultServiceInstance("gateway-2", "api-gateway", "localhost", 8081, false)
                ));
            }
        };
    }
}