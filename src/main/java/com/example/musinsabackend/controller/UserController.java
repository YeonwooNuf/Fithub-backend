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

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        try {
            userService.registerUser(userDto);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "회원가입이 성공적으로 완료되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "회원가입에 실패하였습니다."
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDto userDto) {
        User user = userService.findUserByUsername(userDto.getUsername());

        if (user != null && user.getPassword().equals(userDto.getPassword())) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "성공적으로 로그인 되었습니다.",
                    "nickname", user.getNickname() // 닉네임 반환
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "아이디 및 비밀번호가 일치하지 않습니다."
            ));
        }
    }

}
