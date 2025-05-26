package com.healthcare.orders.service;

import com.healthcare.orders.dto.OrderRequest;
import com.healthcare.orders.dto.OrderResponse;
import java.util.concurrent.CompletableFuture;

public interface OrderService {
    CompletableFuture<OrderResponse> createOrderAsync(OrderRequest request);
    CompletableFuture<OrderResponse> getOrderByIdAsync(Long id);
    CompletableFuture<Void> cancelOrderAsync(Long id);
}