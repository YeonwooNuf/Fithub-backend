package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // ✅ 결제 ID 중복 여부 확인
    boolean existsByPaymentId(String paymentId);
}
