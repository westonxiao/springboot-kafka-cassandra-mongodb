package com.healthcare.common.events;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class OrderEvent {
    private final String orderId;
    private final String userId;
    private final String status;
    private final Instant timestamp;

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    private final Map<String, Object> additionalData;
    // Builder-style method for additional data
    public OrderEvent withAdditionalData(String key, Object value) {
        this.additionalData.put(key, value);
        return this;
    }
    public Map<String, Object> getAdditionalData() {
        return new HashMap<>(additionalData); // Return a copy for immutability
    }
    public OrderEvent(String orderId, String userId, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.timestamp = Instant.now();
        this.additionalData = new HashMap<>();
    }


}