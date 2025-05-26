package com.healthcare.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/order")
    public Mono<String> orderFallback() {
        return Mono.just("Order service is taking too long to respond or is down. Please try again later");
    }

    @GetMapping("/payment")
    public Mono<String> paymentFallback() {
        return Mono.just("Payment service is taking too long to respond or is down. Please try again later");
    }

    @GetMapping("/product")
    public Mono<String> productFallback() {
        return Mono.just("Catalog service is taking too long to respond or is down. Please try again later");
    }

    @GetMapping("/analytics")
    public Mono<String> analyticsFallback() {
        return Mono.just("Analytics service is taking too long to respond or is down. Please try again later");
    }
}