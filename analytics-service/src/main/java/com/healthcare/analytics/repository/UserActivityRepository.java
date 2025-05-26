package com.healthcare.analytics.repository;

import com.healthcare.analytics.model.UserActivity;
import com.healthcare.analytics.model.UserActivityKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserActivityRepository extends CassandraRepository<UserActivity, UserActivityKey> {

    @Query("SELECT * FROM user_activities WHERE user_id = ?0 AND timestamp >= ?1 AND timestamp <= ?2")
    List<UserActivity> findByUserIdAndTimeRange(UUID userId, Instant start, Instant end);

    @Query("SELECT * FROM user_activities WHERE action_type = ?0 ALLOW FILTERING")
    List<UserActivity> findByActionType(String actionType);

    @Query("SELECT COUNT(*) FROM user_activities WHERE user_id = ?0")
    long countActivitiesByUser(UUID userId);

    List<UserActivity> findByUserIdAndActivityTimeBetween(String userId, LocalDate startDate, LocalDate endDate);
}