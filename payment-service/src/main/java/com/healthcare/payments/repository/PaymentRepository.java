package com.healthcare.payments.repository;

import com.healthcare.payments.model.Payment;
import com.healthcare.payments.model.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Basic CRUD operations are inherited from JpaRepository

    // Custom query methods
    Optional<Payment> findByOrderId(String orderId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT p FROM Payment p WHERE p.amount >= :minAmount AND p.amount <= :maxAmount")
    List<Payment> findPaymentsInAmountRange(@Param("minAmount") BigDecimal minAmount,
                                            @Param("maxAmount") BigDecimal maxAmount);

    @Modifying
    @Query("UPDATE Payment p SET p.status = :status WHERE p.orderId = :orderId")
    int updatePaymentStatus(@Param("orderId") String orderId,
                            @Param("status") PaymentStatus status);

    @Query(value = "SELECT * FROM payments WHERE transaction_reference IS NOT NULL",
            nativeQuery = true)
    List<Payment> findPaymentsWithTransactionReference();

    // Count methods
    long countByStatus(PaymentStatus status);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.createdAt > :sinceDate")
    long countPaymentsSince(@Param("sinceDate") LocalDateTime sinceDate);
}