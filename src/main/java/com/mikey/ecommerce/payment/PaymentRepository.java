package com.mikey.ecommerce.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);

    @Query("""
            SELECT p
            FROM Payment p
            ORDER BY p.paidAt DESC
            """)
    List<Payment> findAllLatestFirst();
}
