package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.Coupon;
import com.example.musinsabackend.model.CouponDistributionType;
import com.example.musinsabackend.model.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    // ✅ 자동 지급 쿠폰 조회
    List<Coupon> findByDistributionType(CouponDistributionType distributionType);

    // ✅ 수동 쿠폰 코드로 쿠폰 찾기
    Optional<Coupon> findByCouponCode(String couponCode);

    // ✅ 쿠폰 ID로 조회
    Optional<Coupon> findById(Long couponId);

    // ✅ 특정 쿠폰을 보유한 사용자 목록 조회 (관리자용)
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.coupon = :coupon")
    List<UserCoupon> findUserCouponsByCoupon(@Param("coupon") Coupon coupon);
}
