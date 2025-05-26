package com.healthcare.orders.model;

public enum OrderStatus {
    CREATED,
    PROCESSING,
    COMPLETED,
    CANCELLED, FAILED;

    public String getName() {
        return this.name();
    }
}