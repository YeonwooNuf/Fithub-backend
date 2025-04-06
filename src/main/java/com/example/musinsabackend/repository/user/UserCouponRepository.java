package com.example.musinsabackend.repository.user;

import com.example.musinsabackend.model.coupon.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    List<UserCoupon> findAllByIdIn(List<Long> ids);
}
