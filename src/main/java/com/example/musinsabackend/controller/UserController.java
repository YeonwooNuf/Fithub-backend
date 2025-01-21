package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.UserDto;
import com.example.musinsabackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    // Add endpoints for OAuth login (Kakao, Google)
}