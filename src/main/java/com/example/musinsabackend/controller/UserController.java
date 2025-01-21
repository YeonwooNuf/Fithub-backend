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
    public String registerUser(@RequestBody UserDto userDto) {
        userService.registerUser(userDto);
        return "User registered successfully!";
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDto userDto) {
        User user = userService.findUserByUsername(userDto.getUsername());

        if (user != null && user.getPassword().equals(userDto.getPassword())) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Login successful!",
                    "nickname", user.getNickname() // 닉네임 반환
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Invalid username or password!"
            ));
        }
    }

}
