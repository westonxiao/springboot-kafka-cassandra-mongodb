package com.healthcare.orders.service;

import com.healthcare.orders.model.Order;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// In LegacyPaymentGatewayClient.java
public class LegacyPaymentClient {
    private final ExecutorService blockingPool =
            Executors.newCachedThreadPool();

/*    public CompletableFuture<PaymentResult> processPayment(Order order) {
        return CompletableFuture.supplyAsync(() -> {
            // Wraps synchronous SDK
            return legacySdk.processPaymentSync(order);
        }, blockingPool);
    }*/
}