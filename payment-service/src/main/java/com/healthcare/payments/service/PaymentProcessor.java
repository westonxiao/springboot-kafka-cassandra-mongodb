package com.healthcare.payments.service;

import com.healthcare.common.dto.PaymentRequest;
import com.healthcare.payments.model.Payment;
import com.healthcare.payments.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
public class PaymentProcessor {

    private final PaymentRepository paymentRepository;
    private final ExecutorService executorService;

    public PaymentProcessor(PaymentRepository paymentRepository,
                            @Qualifier("paymentProcessingExecutor") ExecutorService executorService) {
        this.paymentRepository = paymentRepository;
        this.executorService = executorService;
    }

    public CompletableFuture<Payment> processPaymentAsync(PaymentRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            Payment payment = new Payment(request.getOrderId(), request.getAmount(), Payment.PaymentStatus.PENDING);
            payment = paymentRepository.save(payment);

            // Simulate payment processing
            try {
                Thread.sleep(1000); // Mock external payment processing
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
            } catch (Exception e) {
                payment.setStatus(Payment.PaymentStatus.FAILED);
            }

            return paymentRepository.save(payment);
        }, executorService);
    }
}