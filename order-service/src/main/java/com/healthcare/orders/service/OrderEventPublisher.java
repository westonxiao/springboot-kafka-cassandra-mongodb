package com.healthcare.orders.service;

import com.healthcare.common.events.OrderEvent;
import com.healthcare.orders.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
public class OrderEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(OrderEventPublisher.class);

    // Kafka topics
    private static final String ORDER_STATUS_TOPIC = "order-status-events";
    private static final String ORDER_FAILURE_TOPIC = "order-failure-events";
    private static final String ORDER_PROCESSING_TOPIC = "order-processing-events";

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderEventPublisher(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderStatusUpdate(Order order) {
        OrderEvent event = buildOrderEvent(order, "STATUS_UPDATE");
        sendEvent(ORDER_STATUS_TOPIC, order.getUserId(), event, "status update");
    }

    public void publishOrderFailed(Order order, String failureReason) {
        OrderEvent event = buildOrderEvent(order, "FAILED");
        sendEvent(ORDER_FAILURE_TOPIC, order.getUserId(), event, "failure");
    }

    public void publishOrderProcessingStarted(Order order) {
        OrderEvent event = buildOrderEvent(order, "PROCESSING_STARTED");
        sendEvent(ORDER_PROCESSING_TOPIC, order.getUserId(), event, "processing start");
    }

    private OrderEvent buildOrderEvent(Order order, String eventType) {
        return new OrderEvent(
                order.getId().toString(),
                order.getUserId(),
                order.getStatus().name()
        );
    }

    private void sendEvent(String topic, String key, OrderEvent event, String eventDescription) {
        try {
            ListenableFuture<SendResult<String, OrderEvent>> future =
                    kafkaTemplate.send(topic, key, event);

            future.addCallback(new ListenableFutureCallback<SendResult<String, OrderEvent>>() {
                @Override
                public void onSuccess(SendResult<String, OrderEvent> result) {
                    logger.debug("Successfully published {} event for order {} to topic {}",
                            eventDescription, event.getOrderId(), topic);
                }

                @Override
                public void onFailure(Throwable ex) {
                    logger.error("Failed to publish {} event for order {} to topic {}",
                            eventDescription, event.getOrderId(), topic, ex);
                    // Add retry logic or dead-letter queue handling here
                }
            });
        } catch (Exception ex) {
            logger.error("Unexpected error while publishing {} event for order {}",
                    eventDescription, event.getOrderId(), ex);
        }
    }
}