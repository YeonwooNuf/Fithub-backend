package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    // 특정 사용자의 쿠폰 목록 조회
    List<Coupon> findByUser_Username(String username);

    // 특정 사용자의 특정 쿠폰 조회
    Optional<Coupon> findByIdAndUser_Username(Long id, String username);
}
