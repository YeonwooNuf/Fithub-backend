package com.example.musinsabackend.service;

import com.example.musinsabackend.model.Coupon;
import com.example.musinsabackend.model.Payment;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.repository.PaymentRepository;
import com.example.musinsabackend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointService pointService;

    @Autowired
    private CouponService couponService;

    private static final double POINT_REWARD_RATE = 0.05;

    public int processPayment(Long userId, int totalAmount, int pointUsage, Long couponId) {
        int couponDiscount = 0;

        // ✅ 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // ✅ 포인트 차감
        if (pointUsage > 0) {
            pointService.deductPoints(userId, pointUsage);
        }

        // ✅ 쿠폰 할인 적용
        if (couponId != null) {
            Coupon coupon = couponService.validateAndUseCoupon(userId, couponId);
            couponDiscount = coupon.getDiscount();
        }

        // ✅ 최종 결제 금액 계산
        int finalAmount = totalAmount - (pointUsage + couponDiscount);
        if (finalAmount < 0) finalAmount = 0;

        // ✅ 적립금 계산
        int rewardPoints = (int) (finalAmount * POINT_REWARD_RATE);
        pointService.addPoints(userId, rewardPoints, "결제 적립");

        // ✅ 결제 내역 저장
        Payment payment = new Payment();
        payment.setUser(user); // ✅ userId 대신 User 객체 저장
        payment.setTotalAmount(totalAmount);
        payment.setPointUsed(pointUsage);
        payment.setCouponDiscount(couponDiscount);
        payment.setFinalAmount(finalAmount);
        payment.setRewardPoints(rewardPoints);

        paymentRepository.save(payment);

        log.info("✅ 결제 처리 완료: 사용자={}, 최종 결제 금액={}, 적립금={}", userId, finalAmount, rewardPoints);

        return finalAmount;
    }
}
