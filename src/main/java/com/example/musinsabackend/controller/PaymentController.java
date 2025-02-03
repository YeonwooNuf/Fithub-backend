package com.example.musinsabackend.controller;

import com.example.musinsabackend.jwt.JwtTokenProvider;
import com.example.musinsabackend.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider; // ✅ JWT 파싱을 위한 TokenProvider 추가

    /**
     * 결제 처리
     * @param token Authorization 헤더로 전달받은 JWT 토큰
     * @param totalAmount 총 결제 금액
     * @param pointUsage 사용 포인트 금액
     * @param couponId 사용 쿠폰 ID (선택적)
     * @return 최종 결제 금액 및 적립금 정보
     */
    @PostMapping
    public ResponseEntity<?> processPayment(
            @RequestHeader("Authorization") String token,
            @RequestParam int totalAmount,
            @RequestParam(required = false, defaultValue = "0") int pointUsage,
            @RequestParam(required = false) Long couponId
    ) {
        try {
            log.info("결제 요청: 총 금액={}, 사용 포인트={}, 쿠폰 ID={}", totalAmount, pointUsage, couponId);

            // ✅ JWT에서 userId 가져오기
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            log.info("결제 요청 - 사용자 ID: {}", userId);

            // ✅ 결제 처리
            int finalAmount = paymentService.processPayment(userId, totalAmount, pointUsage, couponId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "결제가 성공적으로 완료되었습니다.",
                    "finalAmount", finalAmount
            ));
        } catch (IllegalArgumentException e) {
            log.error("결제 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("결제 처리 중 오류 발생", e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "결제 처리 중 문제가 발생하였습니다."
            ));
        }
    }
}
