package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.UserDto;
import com.example.musinsabackend.jwt.JwtTokenProvider;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        try {
            userService.registerUser(userDto);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "회원가입이 완료되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> requestBody) {
        try {
            String username = requestBody.get("username");
            String password = requestBody.get("password");

            String token = userService.loginUser(username, password);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "로그인 성공",
                    "token", token
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // 마이페이지 데이터 조회
    @GetMapping("/mypage")
    public ResponseEntity<?> getMyPage(@RequestHeader("Authorization") String token) {
        try {
            String username = jwtTokenProvider.getUsernameFromToken(token);

            UserDto user = userService.findUserByUsername(username);

            // 적립금 합산
            int totalPoints = user.getPoints().stream()
                    .mapToInt(point -> point.getAmount())
                    .sum();

            // 사용되지 않은 쿠폰 개수 계산
            long unusedCoupons = user.getCoupons().stream()
                    .filter(coupon -> !coupon.isUsed())
                    .count();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "username", user.getUsername(),
                    "nickname", user.getNickname(),
                    "profileImageUrl", user.getProfileImageUrl(),
                    "totalPoints", totalPoints,
                    "unusedCoupons", unusedCoupons
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "유효하지 않은 요청입니다."
            ));
        }
    }

}
