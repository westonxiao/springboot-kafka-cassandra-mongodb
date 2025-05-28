package com.healthcare.orders.service;

import com.healthcare.orders.model.Order;
import com.healthcare.orders.model.OrderSaga;
import com.healthcare.orders.model.OrderSagaStatus;
import com.healthcare.orders.repository.OrderSagaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class OrderSagaOrchestrator {

    @Autowired
    private OrderService orderService;

    @Autowired
    private InventoryServiceClient inventoryServiceClient;

    @Autowired
    private PaymentServiceClient paymentServiceClient;

    @Autowired
    private OrderSagaRepository sagaRepository;

    @Transactional
    public void createOrderSaga(Order order) {
        OrderSaga saga = new OrderSaga(UUID.randomUUID(), "ORDER_CREATION", OrderSagaStatus.PENDING, UUID.randomUUID());
        sagaRepository.save(saga);

        try {
            // Step 1: Create order (local transaction)
            //orderService.createOrderAsync(order);

            // Step 2: Reserve inventory
            inventoryServiceClient.reserveItems(order);
            saga.setStatus(OrderSagaStatus.COMPLETED);
            sagaRepository.save(saga);

            // Step 3: Process payment
            paymentServiceClient.processPayment(order);

        } catch (Exception e) {
            // Start compensation
            saga.setStatus(OrderSagaStatus.COMPENSATING);
            sagaRepository.save(saga);

            compensateOrderCreation(order, saga);

            saga.setStatus(OrderSagaStatus.COMPENSATED);
            sagaRepository.save(saga);
            throw new RuntimeException("Order creation failed and was compensated", e);
        }
    }

    private void compensateOrderCreation(Order order, OrderSaga saga) {
        try {
            // Reverse payment if it was processed
            if (saga.getStatus() == OrderSagaStatus.COMPLETED) {
                //cancel payment
                //paymentServiceClient.cancelPayment(order.getId());
            }

            // Release inventory
            inventoryServiceClient.releaseItems(order);

            // Cancel order
            orderService.cancelOrderAsync(order.getId());

        } catch (Exception e) {
            // Log compensation failure
            // In a real system, you'd want retry logic here
            throw new RuntimeException("Compensation failed", e);
        }
    }
}
