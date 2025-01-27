package com.example.musinsabackend.service;

import com.example.musinsabackend.model.Coupon;
import com.example.musinsabackend.model.Payment;
import com.example.musinsabackend.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PointService pointService;

    @Autowired
    private CouponService couponService;

    // 적립 비율 (예: 5%)
    private static final double POINT_REWARD_RATE = 0.05;

    public int processPayment(String username, int totalAmount, int pointUsage, Long couponId) {
        int couponDiscount = 0;

        // 포인트 차감
        if (pointUsage > 0) {
            pointService.deductPoints(username, pointUsage);
        }

        // 쿠폰 할인
        if (couponId != null) {
            Coupon coupon = couponService.validateAndUseCoupon(username, couponId);
            couponDiscount = coupon.getDiscount();
        }

        // 최종 결제 금액 계산
        int finalAmount = totalAmount - (pointUsage + couponDiscount);
        if (finalAmount < 0) finalAmount = 0; // 결제 금액이 0보다 작을 수 없음

        // 적립금 계산
        int rewardPoints = (int) (finalAmount * POINT_REWARD_RATE);
        pointService.addPoints(username, rewardPoints, "결제 적립"); // 적립금 추가

        // 결제 내역 저장
        Payment payment = new Payment();
        payment.setUsername(username);
        payment.setTotalAmount(totalAmount);
        payment.setPointUsed(pointUsage);
        payment.setCouponDiscount(couponDiscount);
        payment.setFinalAmount(finalAmount);
        payment.setRewardPoints(rewardPoints); // 적립금 기록

        paymentRepository.save(payment);

        log.info("결제 처리 완료: 사용자={}, 최종 결제 금액={}, 적립금={}", username, finalAmount, rewardPoints);

        return finalAmount;
    }
}
