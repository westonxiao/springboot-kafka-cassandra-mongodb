package com.healthcare.common.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private String paymentId;
    private String orderId;
    private String status;
    private LocalDateTime processedAt;
}