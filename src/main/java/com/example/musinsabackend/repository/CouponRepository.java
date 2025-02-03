package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.Coupon;
import com.example.musinsabackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    // ✅ 특정 사용자의 쿠폰 목록 가져오기 (userId 기반)
    List<Coupon> findByUser(User user);

    // ✅ 특정 사용자의 특정 쿠폰 찾기 (userId 기반)
    Optional<Coupon> findByIdAndUser(Long couponId, User user);

    // 특정 사용자의 쿠폰 개수 조회
    @Query("SELECT COUNT(c) FROM Coupon c WHERE c.user.userId = :userId")
    int countCouponsByUserId(@Param("userId") Long userId);

}
