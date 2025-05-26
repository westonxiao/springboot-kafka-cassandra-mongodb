package com.healthcare.orders.service;

import com.healthcare.orders.model.Order;
import com.healthcare.orders.model.OrderStatus;
import com.healthcare.orders.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class OrderStatusUpdater {

    private final OrderRepository orderRepository;
    private final ExecutorService statusUpdateExecutor;

    public OrderStatusUpdater(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        this.statusUpdateExecutor = Executors.newFixedThreadPool(4);
    }

    public void bulkUpdateStatuses(List<Long> orderIds, OrderStatus newStatus) {
        orderIds.forEach(orderId ->
                statusUpdateExecutor.submit(() ->
                        updateSingleOrderStatus(orderId, newStatus)
                )
        );
    }

    @Transactional
    protected void updateSingleOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NullPointerException());

        order.setStatus(newStatus);
        orderRepository.save(order);
    }
}