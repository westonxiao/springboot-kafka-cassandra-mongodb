package com.healthcare.analytics.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Table("analytics_views")
public class AnalyticsView {

    @PrimaryKey
    private AnalyticsViewKey key;

    @Column("total_events")
    private long totalEvents;

    @Column("total_value")
    private BigDecimal totalValue;

    // Constructors
    public AnalyticsView() {}

    public AnalyticsView(AnalyticsViewKey key, long totalEvents, BigDecimal totalValue) {
        this.key = key;
        this.totalEvents = totalEvents;
        this.totalValue = totalValue;
    }

    // Getters and setters
    public AnalyticsViewKey getKey() { return key; }
    public void setKey(AnalyticsViewKey key) { this.key = key; }
    public long getTotalEvents() { return totalEvents; }
    public void setTotalEvents(long totalEvents) { this.totalEvents = totalEvents; }
    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    // equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalyticsView that = (AnalyticsView) o;
        return totalEvents == that.totalEvents &&
                Objects.equals(key, that.key) &&
                Objects.equals(totalValue, that.totalValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, totalEvents, totalValue);
    }
}