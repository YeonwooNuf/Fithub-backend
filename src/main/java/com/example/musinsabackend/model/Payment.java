package com.example.musinsabackend.model;

import com.example.musinsabackend.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
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
    @Column(columnDefinition = "TEXT")
    private String usedCoupons; // 사용한 쿠폰 정보 (JSON 저장)

    @Column(nullable = false)
    private String status; // 결제 상태 ("PAID")

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 결제한 사용자 정보

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 결제 일시

    // 기본 생성자
    public Payment() {}

    // 전체 필드를 초기화하는 생성자
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

    // ✅ 저장 전 createdAt 및 누락 방지용 기본값 세팅
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "PAID";
        if (this.earnedPoints == null) this.earnedPoints = 0;
    }

    // ✅ 디버깅을 위한 toString
    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", totalAmount=" + totalAmount +
                ", finalAmount=" + finalAmount +
                ", usedPoints=" + usedPoints +
                ", earnedPoints=" + earnedPoints +
                ", usedCoupons='" + usedCoupons + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
