package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // ✅ 결제 ID 중복 여부 확인
    boolean existsByPaymentId(String paymentId);

    // ✅ 사용자 ID로 주문 내역 조회
    List<Order> findByUser_UserId(Long userId);

    @EntityGraph(attributePaths = "usedCoupons")
    Optional<Order> findWithUsedCouponsById(Long id);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.usedCoupons WHERE o.id = :orderId")
    Optional<Order> findWithCouponsById(@Param("orderId") Long orderId);
}
