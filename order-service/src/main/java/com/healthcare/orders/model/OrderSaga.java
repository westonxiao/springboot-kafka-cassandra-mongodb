package com.healthcare.orders.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.util.UUID;

@Table("order_distributed_transaction_sagas")
public class OrderSaga {
    @PrimaryKey
    private UUID id;
    private String type;
    private OrderSagaStatus status;
    private UUID orderId;

    // constructors, getters, setters
    public OrderSaga() {}

    public OrderSaga(UUID id, String type, OrderSagaStatus status, UUID orderId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.orderId = orderId;
    }

    public OrderSagaStatus getStatus() {
        return status;
    }

    public void setStatus(OrderSagaStatus status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // getters and setters...
}