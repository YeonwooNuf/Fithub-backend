package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.UserDto;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        try {
            log.info("회원가입 요청: {}", userDto);
            userService.registerUser(userDto);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "회원가입이 성공적으로 완료되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            log.error("회원가입 실패 - 잘못된 요청: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("회원가입 중 서버 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "회원가입 중 문제가 발생하였습니다."
            ));
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDto userDto) {
        try {
            log.info("로그인 요청: " + userDto.getUsername());
            String token = userService.loginUser(userDto.getUsername(), userDto.getPassword());
            User user = userService.findUserByUsername(userDto.getUsername());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "로그인 성공",
                    "token", token,
                    "nickname", user.getNickname()
            ));
        } catch (IllegalArgumentException e) {
            log.error("로그인 실패: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("서버 내부 오류: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "로그인 중 문제가 발생하였습니다."
            ));
        }
    }

    // 마이페이지 정보 조회
    @GetMapping("/mypage")
    public ResponseEntity<?> getMyPageData(@RequestHeader("Authorization") String token) {
        try {
            String username = userService.getUsernameFromToken(token);
            User user = userService.findUserByUsername(username);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "username", user.getUsername() != null ? user.getUsername() : "",
                    "nickname", user.getNickname() != null ? user.getNickname() : "",
                    "profileImageUrl", user.getProfileImageUrl() != null ? user.getProfileImageUrl() : "",
                    "points", user.getPoints() != null ? user.getPoints() : 0,
                    "coupons", user.getCoupons() != null ? user.getCoupons() : 0
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("마이페이지 조회 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "마이페이지 조회 중 문제가 발생하였습니다."
            ));
        }
    }


    // 사용자 상세 정보 조회
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserDetails(
            @PathVariable String username,
            @RequestHeader("Authorization") String token) {
        try {
            if (!userService.validateToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "success", false,
                        "message", "인증에 실패하였습니다."
                ));
            }

            User user = userService.findUserByUsername(username);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "username", user.getUsername(),
                    "nickname", user.getNickname(),
                    "profileImageUrl", user.getProfileImageUrl(),
                    "points", user.getPoints(),
                    "coupons", user.getCoupons()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "사용자 정보 조회 중 문제가 발생하였습니다."
            ));
        }
    }

}
