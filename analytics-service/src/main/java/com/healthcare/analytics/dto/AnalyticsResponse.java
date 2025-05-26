package com.healthcare.analytics.dto;

import java.math.BigDecimal;
import java.util.Map;

public class AnalyticsResponse {

    private long totalEvents;
    private BigDecimal totalRevenue;
    private Map<String, Long> eventsByType;
    private Map<String, BigDecimal> revenueByCategory;

    // Constructors
    public AnalyticsResponse() {}

    public AnalyticsResponse(long totalEvents, BigDecimal totalRevenue,
                             Map<String, Long> eventsByType,
                             Map<String, BigDecimal> revenueByCategory) {
        this.totalEvents = totalEvents;
        this.totalRevenue = totalRevenue;
        this.eventsByType = eventsByType;
        this.revenueByCategory = revenueByCategory;
    }

    // Getters and Setters
    public long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Map<String, Long> getEventsByType() {
        return eventsByType;
    }

    public void setEventsByType(Map<String, Long> eventsByType) {
        this.eventsByType = eventsByType;
    }

    public Map<String, BigDecimal> getRevenueByCategory() {
        return revenueByCategory;
    }

    public void setRevenueByCategory(Map<String, BigDecimal> revenueByCategory) {
        this.revenueByCategory = revenueByCategory;
    }
}