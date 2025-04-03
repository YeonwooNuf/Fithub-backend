package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.UserDto;
import com.example.musinsabackend.jwt.JwtTokenProvider;
import com.example.musinsabackend.model.user.User;
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
    private static final String UPLOAD_DIR = "/app/uploads/profile-images/";
    private static final String DEFAULT_PROFILE_IMAGE = "/app/uploads/profile-images/default-profile.jpg";

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // ✅ 회원가입
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

    // ✅ 로그인
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> requestBody) {
        try {
            String username = requestBody.get("username");
            String password = requestBody.get("password");

            UserDto user = userService.findUserByUsername(username);
            String token = userService.loginUser(username, password);
            System.out.println("로그인 응답 - 발급된 토큰: " + token);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "로그인 성공",
                    "token", token,
                    "role", user.getRole()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ✅ 마이페이지 데이터 조회
    @GetMapping("/mypage")
    public ResponseEntity<?> getMyPage(@RequestHeader(value = "Authorization", required = false) String token) {

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "유효하지 않은 요청입니다."
            ));
        }
        try {
            String username = jwtTokenProvider.getUsernameFromToken(token.substring(7)); // Bearer 제거
            UserDto user = userService.findUserByUsername(username);
            int couponCount = userService.getUserCouponCount(user.getUserId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "userId", user.getUserId(),
                    "username", user.getUsername(),
                    "phone", user.getPhone(),
                    "nickname", user.getNickname(),
                    "unusedCoupons", couponCount,       // 사용하지 않은 쿠폰 개수
                    "totalPoints", user.getPoints(),    // 적립금
                    "role", user.getRole(),
                    "profileImageUrl", "/uploads/profile-images/" + user.getProfileImageUrl() // ✅ 추가
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "유효하지 않은 요청입니다."
            ));
        }
    }

    // ✅ 사용자의 프로필 이미지 조회
    @GetMapping("/mypage/profile-image")
    public ResponseEntity<?> getUserProfileImage(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "유효하지 않은 요청입니다."
            ));
        }
        try {
            String username = jwtTokenProvider.getUsernameFromToken(token.substring(7)); // Bearer 제거
            UserDto user = userService.findUserByUsername(username);
            String profileImageUrl = "/uploads/profile-images/" + user.getProfileImageUrl();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "profileImageUrl", profileImageUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "프로필 이미지를 불러오는 중 오류가 발생했습니다."
            ));
        }
    }
}
