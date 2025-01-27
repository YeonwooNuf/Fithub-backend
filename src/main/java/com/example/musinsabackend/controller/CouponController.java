package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.CouponDto;
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

    // 사용자 보유 쿠폰 조회
    @GetMapping
    public ResponseEntity<?> getUserCoupons(@RequestHeader("Authorization") String token) {
        try {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            List<CouponDto> userCoupons = couponService.getUserCoupons(username);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "사용자의 쿠폰 목록을 가져왔습니다.",
                    "coupons", userCoupons
            ));
        } catch (Exception e) {
            log.error("쿠폰 조회 중 오류 발생: ", e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "쿠폰 조회 중 문제가 발생하였습니다."
            ));
        }
    }

    // 쿠폰 사용
    @PostMapping("/use")
    public ResponseEntity<?> useCoupon(@RequestHeader("Authorization") String token, @RequestBody Map<String, Long> request) {
        try {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            Long couponId = request.get("couponId");

            couponService.useCoupon(username, couponId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "쿠폰을 성공적으로 사용하였습니다."
            ));
        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청으로 쿠폰 사용 실패: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("쿠폰 사용 중 오류 발생: ", e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "쿠폰 사용 중 문제가 발생하였습니다."
            ));
        }
    }

}
