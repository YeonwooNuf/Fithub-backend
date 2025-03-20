package com.example.musinsabackend.model;

import com.example.musinsabackend.model.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String paymentId; // 결제 고유 ID

    @Column(nullable = false)
    private Double totalAmount; // 원래 결제 금액 (할인 전)

    @Column(nullable = false)
    private Double finalAmount; // 최종 결제 금액 (할인 후)

    @Column(nullable = false)
    private Integer usedPoints; // 사용한 포인트

    @Column(nullable = false)
    private Integer earnedPoints; // 결제 적립 포인트 (최종 결제 금액 * 1%)

    @Lob
    @Column(columnDefinition = "TEXT") // ✅ JSON 데이터 저장
    private String usedCoupons; // 사용한 쿠폰 정보 (JSON 저장)

    @Column(nullable = false)
    private String status; // 결제 상태 ("PAID")

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 결제한 사용자 정보

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 결제 일시

    public Payment() {}

    public Payment(String paymentId, Double totalAmount, Double finalAmount, Integer usedPoints, Integer earnedPoints,
                   String usedCoupons, String status, User user) {
        this.paymentId = paymentId;
        this.totalAmount = totalAmount;
        this.finalAmount = finalAmount;
        this.usedPoints = usedPoints;
        this.earnedPoints = earnedPoints;
        this.usedCoupons = usedCoupons;
        this.status = status;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    // ✅ Getter & Setter
    public Long getId() { return id; }
    public String getPaymentId() { return paymentId; }
    public Double getTotalAmount() { return totalAmount; }
    public Double getFinalAmount() { return finalAmount; }
    public Integer getUsedPoints() { return usedPoints; }
    public Integer getEarnedPoints() { return earnedPoints; }
    public String getUsedCoupons() { return usedCoupons; }
    public String getStatus() { return status; }
    public User getUser() { return user; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public void settotalAmount(Double amount) { this.totalAmount = amount; }
    public void setFinalAmount(Double finalAmount) { this.finalAmount = finalAmount; }
    public void setUsedPoints(Integer usedPoints) { this.usedPoints = usedPoints; }
    public void setEarnedPoints(Integer earnedPoints) { this.earnedPoints = earnedPoints; }
    public void setUsedCoupons(String usedCoupons) { this.usedCoupons = usedCoupons; }
    public void setStatus(String status) { this.status = status; }
    public void setUser(User user) { this.user = user; }
}
