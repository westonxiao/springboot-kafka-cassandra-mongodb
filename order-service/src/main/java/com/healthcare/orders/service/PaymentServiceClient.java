package com.healthcare.orders.service;

import com.healthcare.orders.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;

@Component
public class PaymentServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceClient.class);

    private final RestTemplate restTemplate;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    @Value("${payment.service.timeout:5000}")
    private int timeout;

    public PaymentServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

/*    @Retryable(
            value = {PaymentProcessingException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2));*/

    public void processPayment(Order order) throws PaymentProcessingException {
        logger.info("Attempting to process payment for order {}", order.getId());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setUserId(order.getUserId());
            paymentRequest.setOrderId(order.getId().toString());
            paymentRequest.setAmount(order.getAmount());

            HttpEntity<PaymentRequest> entity = new HttpEntity<>(paymentRequest, headers);

            ResponseEntity<PaymentResponse> response = restTemplate.exchange(
                    paymentServiceUrl + "/api/payments",
                    HttpMethod.POST,
                    entity,
                    PaymentResponse.class
            );

            if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new PaymentProcessingException("Payment service returned status: " + response.getStatusCode());
            }

            PaymentResponse paymentResponse = response.getBody();
            if (paymentResponse == null || !"COMPLETED".equals(paymentResponse.getStatus())) {
                throw new PaymentProcessingException("Payment failed with status: " +
                        (paymentResponse != null ? paymentResponse.getStatus() : "null"));
            }

            logger.info("Successfully processed payment for order {}", order.getId());

        } catch (HttpClientErrorException e) {
            logger.error("Client error processing payment for order {}: {}", order.getId(), e.getMessage());
            throw new PaymentProcessingException("Client error: " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            logger.error("Server error processing payment for order {}: {}", order.getId(), e.getMessage());
            throw new PaymentProcessingException("Server error: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            logger.error("Timeout processing payment for order {}: {}", order.getId(), e.getMessage());
            throw new PaymentProcessingException("Payment service timeout", e);
        } catch (Exception e) {
            logger.error("Unexpected error processing payment for order {}: {}", order.getId(), e.getMessage());
            throw new PaymentProcessingException("Unexpected error: " + e.getMessage(), e);
        }
    }

    // DTOs for payment request/response
    private static class PaymentRequest {
        private String orderId;
        private String userId;
        private BigDecimal amount;

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

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    private static class PaymentResponse {
        private String paymentId;
        private String status;
        private String message;

        public String getPaymentId() {
            return paymentId;
        }

        public void setPaymentId(String paymentId) {
            this.paymentId = paymentId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class PaymentProcessingException extends Exception {
        public PaymentProcessingException(String message) {
            super(message);
        }

        public PaymentProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}