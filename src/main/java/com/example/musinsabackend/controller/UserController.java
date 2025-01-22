package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.UserDto;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        try {
            userService.registerUser(userDto);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "회원가입이 성공적으로 완료되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
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
            String token = userService.loginUser(userDto.getUsername(), userDto.getPassword());
            User user = userService.findUserByUsername(userDto.getUsername());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "로그인에 성공하였습니다.",
                    "token", token,
                    "nickname", user.getNickname(),
                    "profileImageUrl", user.getProfileImageUrl(),
                    "points", user.getPoints(),
                    "coupons", user.getCoupons()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "로그인 중 문제가 발생하였습니다."
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
