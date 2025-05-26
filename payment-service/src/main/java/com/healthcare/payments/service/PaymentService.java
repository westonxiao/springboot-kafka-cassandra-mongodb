package com.healthcare.payments.service;

import com.healthcare.payments.model.Payment;
import com.healthcare.payments.model.Payment.PaymentStatus;
import com.healthcare.payments.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


@Service
@Transactional
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, PaymentStatusUpdate> kafkaTemplate;

    public PaymentService(PaymentRepository paymentRepository,
                          KafkaTemplate<String, PaymentStatusUpdate> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public CompletableFuture<Payment> updatePaymentStatus(Long paymentId, PaymentStatus status) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Attempting to update payment status for paymentId: {} to {}", paymentId, status);

                Payment payment = paymentRepository.findById(paymentId)
                        .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));

                // Validate status transition
                validateStatusTransition(payment.getStatus(), status);

                // Update status
                payment.setStatus(status);
                Payment updatedPayment = paymentRepository.save(payment);

                // Publish status update event
                publishStatusUpdateEvent(updatedPayment);

                logger.info("Successfully updated payment status for paymentId: {}", paymentId);
                return updatedPayment;

            } catch (Exception ex) {
                logger.error("Error updating payment status for paymentId: {}", paymentId, ex);
                throw new CompletionException(ex);
            }
        });
    }

    private void validateStatusTransition(PaymentStatus currentStatus, PaymentStatus newStatus) {
        // Implement your business rules for valid status transitions
        if (currentStatus == PaymentStatus.COMPLETED && newStatus == PaymentStatus.PENDING) {
            throw new IllegalStateException("Cannot revert a completed payment to pending");
        }
        // Add other validation rules as needed
    }

    private void publishStatusUpdateEvent(Payment payment) {
        try {
            PaymentStatusUpdate event = new PaymentStatusUpdate();
            event.setPaymentId(payment.getId());
            event.setOrderId(payment.getOrderId());
            event.setStatus(payment.getStatus());
            event.setUpdatedAt(payment.getUpdatedAt());

            kafkaTemplate.send("payment-status-updates", payment.getOrderId(), event)
                    .addCallback(
                            result -> logger.debug("Successfully published status update for payment {}", payment.getId()),
                            ex -> logger.error("Failed to publish status update for payment {}", payment.getId(), ex)
                    );
        } catch (Exception ex) {
            logger.error("Error publishing status update event for payment {}", payment.getId(), ex);
            // Consider whether to throw or just log based on your requirements
        }
    }

    // Custom exception
    public static class PaymentNotFoundException extends RuntimeException {
        public PaymentNotFoundException(String message) {
            super(message);
        }
    }

    // Event DTO
    public static class PaymentStatusUpdate {
        private Long paymentId;
        private String orderId;
        private PaymentStatus status;
        private LocalDateTime updatedAt;

        public Long getPaymentId() {
            return paymentId;
        }

        public void setPaymentId(Long paymentId) {
            this.paymentId = paymentId;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public PaymentStatus getStatus() {
            return status;
        }

        public void setStatus(PaymentStatus status) {
            this.status = status;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}
