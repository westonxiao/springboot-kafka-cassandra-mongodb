package com.healthcare.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    //RateLimiterFilter rateLimiterFilter = new RateLimiterFilter();
    //ResponseCacheFilter cacheFilter = new ResponseCacheFilter();
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
//                .route("order-service", r -> r.path("/api/orders/**")
//                        .filters(f -> f.circuitBreaker(c -> c.setName("ordersCB")
//                                .setFallbackUri("forward:/fallback/order")))
//                        .uri("lb://order-service"))
                .route("order-service", r -> r.path("/api/orders/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("ordersCB")
                                //.filter(rateLimiterFilter.apply(new RateLimiterFilter.Config(100)))
                                //.filter(cacheFilter.apply(new ResponseCacheFilter.Config(60)))
                                .setFallbackUri("forward:/fallback/order")))
                                .uri("lb://order-service"))
                .route("payment-service", r -> r.path("/api/payments/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("paymentsCB")
                                .setFallbackUri("forward:/fallback/payment")))
                        .uri("lb://payment-service"))
                .route("catalog-service", r -> r.path("/api/products/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("productsCB")
                                .setFallbackUri("forward:/fallback/product")))
                        .uri("lb://catalog-service"))
                .route("analytics-service", r -> r.path("/api/analytics/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("analyticsCB")
                                .setFallbackUri("forward:/fallback/analytics")))
                        .uri("lb://analytics-service"))
                .build();
    }
}