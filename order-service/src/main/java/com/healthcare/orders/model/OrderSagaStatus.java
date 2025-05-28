package com.healthcare.orders.model;

public enum OrderSagaStatus {
    PENDING,
    COMPLETED,
    FAILED,
    COMPENSATING,
    COMPENSATED
}
