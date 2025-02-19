package com.example.musinsabackend.model;

import com.example.musinsabackend.model.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // ✅ userId 외래키 추가
    private User user; // 결제 사용자

    private int totalAmount; // 총 결제 금액
    private int pointUsed; // 사용한 포인트 금액
    private int couponDiscount; // 쿠폰 할인 금액
    private int finalAmount; // 최종 결제 금액
    private int rewardPoints; // 적립된 포인트 금액
}
