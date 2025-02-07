package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.UserDto;
import com.example.musinsabackend.jwt.JwtTokenProvider;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private static final String UPLOAD_DIR = "/uploads/profile-images/";
    private static final String DEFAULT_PROFILE_IMAGE = "/uploads/profile-images/default-profile.jpg";

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

            User user = userService.findUserByUsername(username);
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
            User user = userService.findUserByUsername(username);
            int couponCount = userService.getUserCouponCount(user.getUserId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "userId", user.getUserId(),
                    "username", user.getUsername(),
                    "nickname", user.getNickname(),
                    "profileImageUrl", user.getProfileImageUrl(),
                    "totalPoints", user.getPoints(), // 적립금
                    "role", user.getRole(),
                    "unusedCoupons", couponCount // 사용하지 않은 쿠폰 개수
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "유효하지 않은 요청입니다."
            ));
        }
    }

    // ✅ 사용자의 프로필 이미지 조회 (프론트에서 추가 요청 가능)
    @GetMapping("/{userId}/profile-image")
    public ResponseEntity<?> getUserProfileImage(@PathVariable Long userId) {
        try {
            User user = userService.findUserById(userId);
            String profileImageUrl = user.getProfileImageUrl();

            // ✅ 프로필 이미지가 없으면 기본 이미지로 대체
            if (profileImageUrl == null || profileImageUrl.isEmpty()) {
                profileImageUrl = DEFAULT_PROFILE_IMAGE;
            } else {
                Path imagePath = Paths.get(UPLOAD_DIR, profileImageUrl);
                if (!Files.exists(imagePath)) {
                    profileImageUrl = DEFAULT_PROFILE_IMAGE;
                }
            }

            return ResponseEntity.ok(Map.of("profileImageUrl", profileImageUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "사용자를 찾을 수 없습니다."));
        }
    }
}
