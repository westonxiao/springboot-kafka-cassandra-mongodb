package com.healthcare.orders.service;

import com.healthcare.common.events.OrderEvent;
import com.healthcare.orders.model.Order;
import com.healthcare.orders.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final OrderProcessingService orderProcessingService;

    public OrderEventConsumer(OrderProcessingService orderProcessingService) {
        this.orderProcessingService = orderProcessingService;
    }

    @KafkaListener(topics = "${kafka.topics.order-requests}", groupId = "${kafka.consumer.group-id}")
    @Transactional
    public void consumeOrderRequest(OrderEvent event, Acknowledgment ack) {
        try {
            if (event == null) {
                throw new Exception("Received null order event");
            }

            logger.info("Processing order request event for order ID: {}", event.getOrderId());

            Order order = convertToOrder(event);
            orderProcessingService.processOrderAsync(order)
                    .thenApply(processedOrder -> {
                        ack.acknowledge();
                        logger.info("Successfully processed order {}", processedOrder.getId());
                        return processedOrder;
                    })
                    .exceptionally(ex -> {
                        logger.error("Failed to process order event {}", event.getOrderId(), ex);
                        // Optionally implement dead-letter queue publishing here
                        ack.acknowledge(); // Still acknowledge to prevent reprocessing
                        return null;
                    });

        } catch (Exception e) {
            logger.error("Unexpected error processing order event", e);
            ack.acknowledge(); // Acknowledge to prevent blocking the queue
        }
    }

    private Order convertToOrder(OrderEvent event) throws Exception {
        try {
            if (event.getOrderId() == null || event.getUserId() == null) {
                throw new Exception("Order event missing required fields");
            }

            Order order = new Order();
            order.setId(Long.parseLong(event.getOrderId()));
            order.setUserId(event.getUserId());
            //order.setAmount(event.getAmount());
            order.setStatus(OrderStatus.CREATED);

            return order;
        } catch (NumberFormatException e) {
            throw new Exception("Invalid order ID format: " + event.getOrderId(), e);
        }
    }
}