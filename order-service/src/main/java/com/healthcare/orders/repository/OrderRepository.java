package com.healthcare.orders.repository;

import com.healthcare.orders.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
//service
//serviceImpl
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(String userId);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt > :since")
    List<Order> findRecentByStatus(
            @Param("status") String status,
            @Param("since") java.time.LocalDateTime since);
}