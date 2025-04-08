package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.CouponDto;
import com.example.musinsabackend.dto.UserCouponDto;
import com.example.musinsabackend.service.CouponService;
import com.example.musinsabackend.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class CouponController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // ✅ 사용자 보유 쿠폰 조회
    @GetMapping
    public ResponseEntity<?> getUserCoupons(@RequestHeader("Authorization") String token) {
        try {
            token = token.replace("Bearer ", "");
            Long userId = jwtTokenProvider.getUserIdFromToken(token);

            // ✅ UserCouponDto 참조 (정상 작동)
            List<UserCouponDto> userCoupons = couponService.getUserCoupons(userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "사용자의 쿠폰 목록을 가져왔습니다.",
                    "coupons", userCoupons
            ));
        } catch (Exception e) {
            log.error("❌ 쿠폰 조회 중 오류 발생: ", e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "쿠폰 조회 중 문제가 발생했습니다."
            ));
        }
    }

    // ✅ 마이페이지에서 보유 쿠폰 개수 조회
    @GetMapping("/count")
    public ResponseEntity<?> getCouponCount(@RequestHeader("Authorization") String token) {
        try {
            token = token.replace("Bearer ", "");
            Long userId = jwtTokenProvider.getUserIdFromToken(token);

            int couponCount = couponService.getCouponCount(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "보유한 쿠폰 개수를 가져왔습니다.",
                    "count", couponCount
            ));
        } catch (Exception e) {
            log.error("❌ 쿠폰 개수 조회 중 오류 발생: ", e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "쿠폰 개수 조회 중 문제가 발생했습니다."
            ));
        }
    }

    // ✅ 사용자 쿠폰 코드 등록 (이벤트 페이지에서 받은 쿠폰 등록)
    @PostMapping("/register")
    public ResponseEntity<?> registerCouponByCode(@RequestHeader("Authorization") String token,
                                                  @RequestBody Map<String, String> request) {
        try {
            token = token.replace("Bearer ", "");
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            String couponCode = request.get("couponCode").toUpperCase();

            CouponDto registeredCoupon = couponService.registerCouponByCode(userId, couponCode);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "쿠폰이 성공적으로 등록되었습니다.",
                    "coupon", registeredCoupon
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("❌ 쿠폰 등록 중 오류 발생: ", e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "쿠폰 등록 중 문제가 발생했습니다."
            ));
        }
    }

    // ✅ 결제 페이지에서 쿠폰 사용 (추후 추가 예정)
    // @PostMapping("/use")
    // public ResponseEntity<?> useCoupon(...) { }

    // ✅ 이벤트 페이지에서 수동 쿠폰 등록 (추후 추가 예정)
    // @PostMapping("/register")
    // public ResponseEntity<?> registerManualCoupon(...) { }
}
