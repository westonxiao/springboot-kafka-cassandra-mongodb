package com.healthcare.orders.service;

import com.healthcare.orders.dto.OrderRequest;
import com.healthcare.orders.dto.OrderResponse;
import com.healthcare.orders.exception.OrderNotFoundException;
import com.healthcare.orders.model.Order;
import com.healthcare.orders.model.OrderStatus;
import com.healthcare.orders.repository.OrderRepository;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private final OrderRepository orderRepository;
    private final OrderProcessingService orderProcessingService;
    private final OrderEventPublisher orderEventPublisher;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderProcessingService orderProcessingService,
                            OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.orderProcessingService = orderProcessingService;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Async
    @Transactional
    @Override
    public CompletableFuture<OrderResponse> createOrderAsync(OrderRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Creating new order for user: {}", request.getUserId());

            Order order = new Order();
            order.setUserId(request.getUserId());
            order.setAmount(request.getAmount());
            order.setStatus(OrderStatus.CREATED);

            Order savedOrder = orderRepository.save(order);
            logger.debug("Order created with ID: {}", savedOrder.getId());

            // Publish creation event
            //orderEventPublisher.publishOrderCreated(savedOrder);

            // Start async processing
            orderProcessingService.processOrderAsync(savedOrder);

            return OrderResponse.fromEntity(savedOrder);
        });
    }

    @Async
    @Transactional(readOnly = true)
    @Override
    public CompletableFuture<OrderResponse> getOrderByIdAsync(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            logger.debug("Fetching order with ID: {}", id);

            return orderRepository.findById(id)
                    .map(OrderResponse::fromEntity)
                    .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));
        });
    }

    @Async
    @Transactional
    @Override
    public CompletableFuture<Void> cancelOrderAsync(Long id) {
        return CompletableFuture.runAsync(() -> {
            logger.info("Attempting to cancel order with ID: {}", id);

            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));

            /*if (!order.getStatus().isCancellable()) {
                throw new IllegalArgumentException("Order cannot be cancelled in current status: " + order.getStatus());
            }*/

            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

            // Publish cancellation event
            //orderEventPublisher.publishOrderCancelled(order);

            logger.info("Successfully cancelled order with ID: {}", id);
        });
    }

    @Autowired
    private KafkaProducer kafkaProducer;

    public Order createOrder(Order order) {
        Order savedOrder = orderRepository.save(order);
        //kafkaProducer.sendOrderCreatedEvent(savedOrder);
        return savedOrder;
    }

    public void cancelOrder(UUID orderId) {
//        orderRepository.findById(orderId).ifPresent(order -> {
//            //order.setStatus("CANCELLED");
//            orderRepository.save(order);
//            //kafkaProducer.sendOrderCancelledEvent(order);
//        });
    }
}