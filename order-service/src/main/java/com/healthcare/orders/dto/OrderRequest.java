package com.healthcare.orders.dto;


import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Objects;

public class OrderRequest {
    @NotBlank(message = "User ID is required")
    private final String userId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Digits(integer = 10, fraction = 2, message = "Invalid amount format")
    private final BigDecimal amount;

    public OrderRequest(String userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    // equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderRequest that = (OrderRequest) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, amount);
    }

    // toString()
    @Override
    public String toString() {
        return "OrderRequest{" +
                "userId='" + userId + '\'' +
                ", amount=" + amount +
                '}';
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String userId;
        private BigDecimal amount;

        private Builder() {}

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public OrderRequest build() {
            return new OrderRequest(userId, amount);
        }
    }
}