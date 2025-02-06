package com.example.musinsabackend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 쿠폰명

    @Column(nullable = false)
    private int discount; // 할인율 (%)

    @Column(nullable = false)
    private int maxDiscountAmount; // 최대 할인 금액

    @Column(nullable = false)
    private LocalDate expiryDate; // 만료일

    @Column(nullable = false)
    private String description; // 쿠폰 설명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponTarget target; // 적용 대상 (전체 상품, 브랜드, 카테고리)

    private String targetValue; // 브랜드명 또는 카테고리명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponDistributionType distributionType; // 지급 방식 (자동/수동)

    @Column(unique = true)
    private String couponCode; // 수동 등록 시 사용하는 쿠폰 코드 (자동 지급 시 null)

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCoupon> userCoupons; // 유저 쿠폰 목록

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getDiscount() { return discount; }
    public void setDiscount(int discount) { this.discount = discount; }

    public int getMaxDiscountAmount() { return maxDiscountAmount; }
    public void setMaxDiscountAmount(int maxDiscountAmount) { this.maxDiscountAmount = maxDiscountAmount; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public CouponTarget getTarget() { return target; }
    public void setTarget(CouponTarget target) { this.target = target; }

    public String getTargetValue() { return targetValue; }
    public void setTargetValue(String targetValue) { this.targetValue = targetValue; }

    public CouponDistributionType getDistributionType() { return distributionType; }
    public void setDistributionType(CouponDistributionType distributionType) { this.distributionType = distributionType; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public List<UserCoupon> getUserCoupons() { return userCoupons; }
    public void setUserCoupons(List<UserCoupon> userCoupons) { this.userCoupons = userCoupons; }
}
