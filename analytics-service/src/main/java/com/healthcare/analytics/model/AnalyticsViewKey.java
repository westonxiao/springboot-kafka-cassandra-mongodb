package com.healthcare.analytics.model;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import java.time.LocalDate;
import java.util.Objects;

public class AnalyticsViewKey {

    @PrimaryKeyColumn(name = "date", type = PrimaryKeyType.PARTITIONED)
    private LocalDate date;

    @PrimaryKeyColumn(name = "view_type", type = PrimaryKeyType.CLUSTERED)
    private String viewType;

    // Constructors
    public AnalyticsViewKey() {}

    public AnalyticsViewKey(LocalDate date, String viewType) {
        this.date = date;
        this.viewType = viewType;
    }

    // Getters and setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getViewType() { return viewType; }
    public void setViewType(String viewType) { this.viewType = viewType; }

    // equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalyticsViewKey that = (AnalyticsViewKey) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(viewType, that.viewType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, viewType);
    }
}