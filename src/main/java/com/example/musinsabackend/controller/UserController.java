package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.UserDto;
import com.example.musinsabackend.jwt.JwtTokenProvider;
import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<?> getMyPage(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "유효하지 않은 요청입니다."
            ));
        }

        try {
            UserDto user = userService.findUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "사용자를 찾을 수 없습니다."
                ));
            }

            int couponCount = userService.getUserCouponCount(userId);

            String profileImage = user.getProfileImageUrl();
            String profileImageUrl;

            if (profileImage != null && profileImage.startsWith("http")) {
                // 외부 URL (예: 카카오 로그인)
                profileImageUrl = profileImage;
            } else if (profileImage != null && !profileImage.isEmpty()) {
                // 내부에 업로드된 이미지 파일
                profileImageUrl = "/uploads/profile-images/" + profileImage;
            } else {
                // 기본 프로필 이미지
                profileImageUrl = "/uploads/profile-images/default-profile.jpg";
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "userId", user.getUserId() != null ? user.getUserId() : -1,
                    "username", user.getUsername() != null ? user.getUsername() : "unknown",
                    "phone", user.getPhone() != null ? user.getPhone() : "",
                    "nickname", user.getNickname() != null ? user.getNickname() : "",
                    "unusedCoupons", couponCount,
                    "totalPoints", user.getPoints() != null ? user.getPoints() : 0,
                    "role", user.getRole() != null ? user.getRole() : "USER",
                    "profileImageUrl", profileImageUrl != null ? profileImageUrl : "/uploads/profile-images/default-profile.jpg"
            ));

        } catch (Exception e) {
            e.printStackTrace(); // 로그 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "사용자 정보를 불러오는 중 오류가 발생했습니다."
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

            String profileImage = user.getProfileImageUrl();
            String profileImageUrl;

            if (profileImage != null && profileImage.startsWith("http")) {
                // ✅ 외부 이미지인 경우 가공하지 않고 그대로 사용
                profileImageUrl = profileImage;
            } else if (profileImage != null && !profileImage.isEmpty()) {
                profileImageUrl = "/uploads/profile-images/" + profileImage;
            } else {
                profileImageUrl = "/uploads/profile-images/default-profile.jpg";
            }

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
