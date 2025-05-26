package com.healthcare.orders.service;

import com.healthcare.orders.model.Order;
import com.healthcare.orders.model.OrderStatus;
import com.healthcare.orders.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
public class OrderProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(OrderProcessingService.class);

    private final OrderRepository orderRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final InventoryServiceClient inventoryServiceClient;
    private final OrderEventPublisher orderEventPublisher;

    @Autowired
    public OrderProcessingService(
            OrderRepository orderRepository,
            PaymentServiceClient paymentServiceClient,
            InventoryServiceClient inventoryServiceClient,
            OrderEventPublisher orderEventPublisher) {

        Assert.notNull(orderRepository, "OrderRepository must not be null");
        Assert.notNull(paymentServiceClient, "PaymentServiceClient must not be null");
        Assert.notNull(inventoryServiceClient, "InventoryServiceClient must not be null");
        Assert.notNull(orderEventPublisher, "OrderEventPublisher must not be null");

        this.orderRepository = orderRepository;
        this.paymentServiceClient = paymentServiceClient;
        this.inventoryServiceClient = inventoryServiceClient;
        this.orderEventPublisher = orderEventPublisher;

        logger.info("OrderProcessingService initialized with all dependencies");
    }

    @Async("orderProcessingExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Order> processOrderAsync(Order order) {
        // Null check for the input order
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Starting async processing for order ID: {}", order.getId());

                // Verify order exists in database
                Order persistedOrder = orderRepository.findById(order.getId())
                        .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + order.getId()));

                // Step 1: Reserve inventory
                reserveInventory(persistedOrder);

                // Step 2: Process payment
                processPayment(persistedOrder);

                // Step 3: Update order status to PROCESSING
                updateOrderStatus(persistedOrder, OrderStatus.PROCESSING);

                logger.info("Successfully processed order ID: {}", persistedOrder.getId());
                return persistedOrder;

            } catch (InventoryException ex) {
                logger.error("Inventory reservation failed for order ID: {}", order.getId(), ex);
                handleFailedOrder(order, "Inventory reservation failed");
                throw new CompletionException(ex);

            } catch (PaymentException ex) {
                logger.error("Payment processing failed for order ID: {}", order.getId(), ex);
                handleFailedOrder(order, "Payment processing failed");
                throw new CompletionException(ex);

            } catch (Exception ex) {
                logger.error("Unexpected error processing order ID: {}", order.getId(), ex);
                handleFailedOrder(order, "Unexpected processing error");
                throw new CompletionException(ex);
            }
        });
    }

    private void reserveInventory(Order order) throws InventoryException {
        try {
            logger.debug("Reserving inventory for order ID: {}", order.getId());
            inventoryServiceClient.reserveItems(order);
        } catch (Exception ex) {
            throw new InventoryException("Failed to reserve inventory for order " + order.getId(), ex);
        }
    }

    private void processPayment(Order order) throws PaymentException {
        try {
            logger.debug("Processing payment for order ID: {}", order.getId());
            paymentServiceClient.processPayment(order);
        } catch (Exception ex) {
            // Attempt to release any reserved inventory
            try {
                logger.warn("Attempting to release inventory after payment failure for order ID: {}", order.getId());
                inventoryServiceClient.releaseItems(order);
            } catch (Exception releaseEx) {
                logger.error("Failed to release inventory for failed order ID: {}", order.getId(), releaseEx);
            }
            throw new PaymentException("Failed to process payment for order " + order.getId(), ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void updateOrderStatus(Order order, OrderStatus status) {
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        orderEventPublisher.publishOrderStatusUpdate(updatedOrder);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void handleFailedOrder(Order order, String failureReason) {
        try {
            order.setStatus(OrderStatus.FAILED);
            //order.setFailureReason(failureReason);
            Order failedOrder = orderRepository.save(order);
            orderEventPublisher.publishOrderFailed(failedOrder, failureReason);

            // Additional cleanup if needed
            try {
                inventoryServiceClient.releaseItems(order);
            } catch (Exception ex) {
                logger.error("Failed to release inventory for failed order ID: {}", order.getId(), ex);
            }
        } catch (Exception ex) {
            logger.error("Critical error while handling failed order ID: {}", order.getId(), ex);
            throw ex;
        }
    }

    // Custom exceptions
    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(String message) {
            super(message);
        }
    }

    public static class InventoryException extends Exception {
        public InventoryException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class PaymentException extends Exception {
        public PaymentException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}