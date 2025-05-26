package com.healthcare.payments.service;

import com.healthcare.common.dto.PaymentRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentKafkaConsumer {

    private final PaymentProcessor paymentProcessor;

    public PaymentKafkaConsumer(PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }

    @KafkaListener(topics = "payment-requests", groupId = "payment-group")
    public void listen(PaymentRequest request) {
        paymentProcessor.processPaymentAsync(request)
                .thenAccept(payment -> {
                    // Handle successful processing
                })
                .exceptionally(ex -> {
                    // Handle errors
                    return null;
                });
    }
}