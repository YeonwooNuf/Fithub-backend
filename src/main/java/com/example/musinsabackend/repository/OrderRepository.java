package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // ✅ 결제 ID 중복 여부 확인
    boolean existsByPaymentId(String paymentId);

    // ✅ 사용자 ID로 주문 내역 조회
    List<Order> findByUser_UserId(Long userId);
}
