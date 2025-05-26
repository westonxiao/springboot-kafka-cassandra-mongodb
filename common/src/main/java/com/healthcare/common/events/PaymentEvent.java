package com.healthcare.common.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private String paymentId;
    private String orderId;
    private BigDecimal amount;
    private String status; // PENDING, COMPLETED, FAILED
    private Instant timestamp = Instant.now();
}