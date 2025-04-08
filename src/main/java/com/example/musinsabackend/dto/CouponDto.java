package com.example.musinsabackend.dto;

import com.example.musinsabackend.model.coupon.Coupon;
import com.example.musinsabackend.model.coupon.CouponDistributionType;
import com.example.musinsabackend.model.coupon.CouponTarget;
import com.example.musinsabackend.model.coupon.UserCoupon;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CouponDto {
    private Long id;
    private String name;                 // 쿠폰명
    private int discount;                // 할인율
    private int maxDiscountAmount;       // 최대 할인 금액
    private String description;          // 쿠폰 설명
    private LocalDate issuedDate;        // 발급일 (추가)
    private LocalDate expiryDate;        // 만료일
    private boolean isUsed;              // 사용 여부
    private CouponTarget target;         // 적용 대상 (전체 상품, 브랜드, 카테고리)
    private String targetValue;          // 브랜드명 또는 카테고리명
    private CouponDistributionType distributionType; // 지급 방식 (자동/수동)
    private String couponCode;           // 수동 쿠폰의 경우 쿠폰 코드

    private Long userCouponId;           // 사용자 쿠폰 ID

    public CouponDto() {}

    // ✅ 간단한 생성자 추가
    public CouponDto(Long id, String name, int discount, LocalDate expiryDate, boolean isUsed) {
        this.id = id;
        this.name = name;
        this.discount = discount;
        this.expiryDate = expiryDate;
        this.isUsed = isUsed;
    }

    // ✅ 이벤트 추가 수동 쿠폰 선택 시 사용
    public CouponDto(Long id, String name, String couponCode) {
        this.id = id;
        this.name = name;
        this.couponCode = couponCode;
    }

    public CouponDto(Long id, String name, int discount, int maxDiscountAmount, String description,
                     LocalDate issuedDate, LocalDate expiryDate, boolean isUsed, CouponTarget target,
                     String targetValue, CouponDistributionType distributionType, String couponCode) {
        this.id = id;
        this.name = name;
        this.discount = discount;
        this.maxDiscountAmount = maxDiscountAmount;
        this.description = description;
        this.issuedDate = issuedDate;
        this.expiryDate = expiryDate;
        this.isUsed = isUsed;
        this.target = target;
        this.targetValue = targetValue;
        this.distributionType = distributionType;
        this.couponCode = couponCode;
    }

    public static CouponDto fromEntity(Coupon coupon) {
        return new CouponDto(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscount(),
                coupon.getMaxDiscountAmount(),
                coupon.getDescription(),
                null, // issuedDate는 UserCoupon에서 가져와야 하므로 null
                coupon.getExpiryDate(),
                false, // 기본값: 사용하지 않은 쿠폰
                coupon.getTarget(),
                coupon.getTargetValue(),
                coupon.getDistributionType(),
                coupon.getCouponCode()
        );
    }

    public static CouponDto fromUserCoupon(UserCoupon userCoupon) {
        Coupon coupon = userCoupon.getCoupon();

        CouponDto dto = new CouponDto(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscount(),
                coupon.getMaxDiscountAmount(),
                coupon.getDescription(),
                userCoupon.getIssuedDate(),
                userCoupon.getExpiryDate(),
                userCoupon.isUsed(),
                coupon.getTarget(),
                coupon.getTargetValue(),
                coupon.getDistributionType(),
                coupon.getCouponCode()
        );

        dto.setUserCouponId(userCoupon.getId());  // ✅ 핵심
        return dto;
    }

}
