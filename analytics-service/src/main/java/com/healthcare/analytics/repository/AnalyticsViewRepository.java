package com.healthcare.analytics.repository;

import com.healthcare.analytics.model.AnalyticsView;
import com.healthcare.analytics.model.AnalyticsViewKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnalyticsViewRepository extends CassandraRepository<AnalyticsView, AnalyticsViewKey> {

    @Query("SELECT * FROM analytics_views WHERE date = ?0")
    List<AnalyticsView> findByDate(LocalDate date);

    @Query("SELECT * FROM analytics_views WHERE date >= ?0 AND date <= ?1 ALLOW FILTERING")
    List<AnalyticsView> findByDateRange(LocalDate start, LocalDate end);

    @Query("SELECT * FROM analytics_views WHERE view_type = ?0 ALLOW FILTERING")
    List<AnalyticsView> findByViewType(String viewType);
}