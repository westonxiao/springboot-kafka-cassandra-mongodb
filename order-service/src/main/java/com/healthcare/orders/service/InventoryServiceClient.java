package com.healthcare.orders.service;

import com.healthcare.orders.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.UUID;

@Component
public class InventoryServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceClient.class);

    private final RestTemplate restTemplate;

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    @Value("${inventory.service.timeout:3000}")
    private int timeout;

    public InventoryServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Retryable(
            value = {InventoryReservationException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 1.5))

    public void reserveItems(Order order) throws InventoryReservationException {
        logger.info("Attempting to reserve inventory for order {}", order.getId());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            InventoryReservationRequest request = new InventoryReservationRequest(
            );
            request.setOrderId(order.getId().toString());
            request.setUserId(order.getUserId());
            // Add any item details needed

            HttpEntity<InventoryReservationRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<InventoryReservationResponse> response = restTemplate.exchange(
                    inventoryServiceUrl + "/api/inventory/reserve",
                    HttpMethod.POST,
                    entity,
                    InventoryReservationResponse.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new InventoryReservationException("Inventory service returned status: " + response.getStatusCode());
            }

            InventoryReservationResponse reservationResponse = response.getBody();
            if (reservationResponse == null || !reservationResponse.isSuccess()) {
                throw new InventoryReservationException("Inventory reservation failed: " +
                        (reservationResponse != null ? reservationResponse.getMessage() : "null response"));
            }

            logger.info("Successfully reserved inventory for order {}", order.getId());

        } catch (HttpClientErrorException e) {
            logger.error("Client error reserving inventory for order {}: {}", order.getId(), e.getMessage());
            throw new InventoryReservationException("Client error: " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            logger.error("Server error reserving inventory for order {}: {}", order.getId(), e.getMessage());
            throw new InventoryReservationException("Server error: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            logger.error("Timeout reserving inventory for order {}: {}", order.getId(), e.getMessage());
            throw new InventoryReservationException("Inventory service timeout", e);
        } catch (Exception e) {
            logger.error("Unexpected error reserving inventory for order {}: {}", order.getId(), e.getMessage());
            throw new InventoryReservationException("Unexpected error: " + e.getMessage(), e);
        }
    }

    public void releaseItems(Order order) throws InventoryReleaseException {
        logger.info("Releasing inventory for order {}", order.getId());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            InventoryReleaseRequest request = new InventoryReleaseRequest();
            request.setOrderId(order.getId().toString());

            HttpEntity<InventoryReleaseRequest> entity = new HttpEntity<>(request, headers);

            restTemplate.exchange(
                    inventoryServiceUrl + "/api/inventory/release",
                    HttpMethod.POST,
                    entity,
                    Void.class
            );

            logger.info("Successfully released inventory for order {}", order.getId());

        } catch (Exception e) {
            logger.error("Error releasing inventory for order {}: {}", order.getId(), e.getMessage());
            throw new InventoryReleaseException("Failed to release inventory: " + e.getMessage(), e);
        }
    }

    private static class InventoryReservationRequest {
        private String orderId;
        private String userId;
        // Add item details as needed

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    private static class InventoryReservationResponse {
        private boolean success;
        private String message;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    private static class InventoryReleaseRequest {
        private String orderId;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }
    }

    public static class InventoryReservationException extends Exception {
        public InventoryReservationException(String message) {
            super(message);
        }

        public InventoryReservationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class InventoryReleaseException extends Exception {
        public InventoryReleaseException(String message) {
            super(message);
        }

        public InventoryReleaseException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void reserveInventory(Order order) {
        // Send message to inventory service
        kafkaTemplate.send("inventory-reserve", order);
        // In real implementation, you might want to wait for response
    }

    public void releaseInventory(UUID orderId) {
        kafkaTemplate.send("inventory-release", orderId);
    }
}