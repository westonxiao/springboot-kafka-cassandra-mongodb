package com.healthcare.common.dto;

import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;


public class PaymentRequest {
    @NotBlank
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Positive
    @Digits(integer=10, fraction=2)
    private BigDecimal amount;

    @NotBlank
    @Pattern(regexp = "CREDIT_CARD|PAYPAL|BANK_TRANSFER")
    private String paymentMethod;
}