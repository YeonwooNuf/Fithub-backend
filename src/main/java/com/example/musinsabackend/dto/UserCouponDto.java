package com.example.musinsabackend.dto;

import com.example.musinsabackend.model.coupon.Coupon;
import com.example.musinsabackend.model.coupon.UserCoupon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCouponDto {
    private Long userCouponId;     // ✅ 실제 필요한 값
    private Long couponId;         // 쿠폰 템플릿 ID
    private String name;
    private int discount;
    private int maxDiscountAmount;
    private LocalDate expiryDate;
    private String target;
    private String targetValue;

    public UserCouponDto(UserCoupon userCoupon) {
        this.userCouponId = userCoupon.getId();
        Coupon coupon = userCoupon.getCoupon();
        this.couponId = coupon.getId();
        this.name = coupon.getName();
        this.discount = coupon.getDiscount();
        this.maxDiscountAmount = coupon.getMaxDiscountAmount();
        this.expiryDate = coupon.getExpiryDate();
        this.target = coupon.getTarget().name();
        this.targetValue = coupon.getTargetValue();
    }
}
