package com.example.musinsabackend.service;

import com.example.musinsabackend.model.Payment;
import com.example.musinsabackend.model.coupon.Coupon;
import com.example.musinsabackend.model.point.PointReason;
import com.example.musinsabackend.model.point.PointStatus;
import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.repository.PaymentRepository;
import com.example.musinsabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PointService pointService;
    private final CouponService couponService;

    private static final double POINT_REWARD_RATE = 0.05;

    /**
     * ✅ 결제 처리
     */
    @Transactional
    public int processPayment(Long userId, int totalAmount, int pointUsage, Long couponId) {
        int couponDiscount = 0;

        // ✅ 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // ✅ 포인트 차감
        if (pointUsage > 0) {
            pointService.usePoints(userId, pointUsage, PointReason.ORDER_USE, null);
        }

        // ✅ 쿠폰 할인 적용
        if (couponId != null) {
            Coupon coupon = couponService.validateAndUseCoupon(userId, couponId);
            couponDiscount = coupon.getDiscount();
        }

        // ✅ 최종 결제 금액 계산
        int finalAmount = totalAmount - (pointUsage + couponDiscount);
        if (finalAmount < 0) finalAmount = 0;

        // ✅ 적립금 계산 (최종 결제 금액의 5%)
        int rewardPoints = (int) (finalAmount * POINT_REWARD_RATE);
        if (rewardPoints > 0) {
            pointService.earnPoints(userId, rewardPoints, PointReason.PURCHASE_REWARD, null);
        }

        // ✅ 결제 내역 저장
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setTotalAmount(totalAmount);
        payment.setPointUsed(pointUsage);
        payment.setCouponDiscount(couponDiscount);
        payment.setFinalAmount(finalAmount);
        payment.setRewardPoints(rewardPoints);

        paymentRepository.save(payment);

        log.info("✅ 결제 완료: 사용자={}, 최종 결제 금액={}, 적립금={}", userId, finalAmount, rewardPoints);

        return finalAmount;
    }

    /**
     * ✅ 주문 취소 처리
     */
    @Transactional
    public void cancelPayment(Long paymentId) {
        // ✅ 결제 내역 조회
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역을 찾을 수 없습니다."));

        Long userId = payment.getUser().getUserId();

        // ✅ 사용된 포인트 반환
        if (payment.getPointUsed() > 0) {
            pointService.earnPoints(userId, payment.getPointUsed(), PointReason.ORDER_CANCEL, null);
        }

        // ✅ 적립된 포인트 취소
        if (payment.getRewardPoints() > 0) {
            pointService.cancelEarnedPoints(userId, payment.getRewardPoints(), PointReason.PURCHASE_REWARD);
        }

        // ✅ 결제 취소 처리
        payment.setFinalAmount(0);
        payment.setPointUsed(0);
        payment.setRewardPoints(0);
        paymentRepository.save(payment);

        log.info("🚨 주문 취소 완료: 사용자={}, 결제ID={}, 사용 포인트 반환, 적립 포인트 취소", userId, paymentId);
    }
}
