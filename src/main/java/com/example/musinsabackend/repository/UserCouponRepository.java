package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.User;
import com.example.musinsabackend.model.UserCoupon;
import com.example.musinsabackend.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    // 사용자 기능

    // ✅ 특정 사용자의 보유 쿠폰 목록 조회
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.user = :user")
    List<UserCoupon> findUserCouponsByUser(@Param("user") User user);

    // ✅ 특정 사용자의 쿠폰 개수 조회
    @Query("SELECT COUNT(uc) FROM UserCoupon uc WHERE uc.user.userId = :userId")
    int countCouponsByUserId(@Param("userId") Long userId);

    // ✅ 사용자 쿠폰 삭제
    void delete(UserCoupon userCoupon);

    // ✅ 사용자가 특정 쿠폰을 이미 보유하고 있는지 확인 (중복 등록 방지)
    @Query("SELECT COUNT(uc) > 0 FROM UserCoupon uc WHERE uc.user = :user AND uc.coupon = :coupon")
    boolean existsByUserAndCoupon(@Param("user") User user, @Param("coupon") Coupon coupon);
}
