package com.healthcare.orders.dto;

import com.healthcare.orders.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class OrderResponse {
    private final Long id;
    private final String userId;
    private final BigDecimal amount;
    private final String status;
    private final LocalDateTime createdAt;

    public OrderResponse(Long id, String userId, BigDecimal amount, String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Factory method
    public static OrderResponse fromEntity(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getAmount(),
                order.getStatus().name(),
                order.getCreatedAt()
        );
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderResponse that = (OrderResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(status, that.status) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, amount, status, createdAt);
    }

    // toString()
    @Override
    public String toString() {
        return "OrderResponse{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}