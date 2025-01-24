package com.example.musinsabackend.controller;

import com.example.musinsabackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/points")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class PointsController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getPoints(@RequestParam String username, @RequestHeader("Authorization") String token) {

        try {
            boolean isValidToken = userService.validateToken(token, username);
            if (!isValidToken) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Invalid token"));
            }

            int points = userService.getUserPoints(username);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Points retrieved successfully",
                    "points", points
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error fetching points"));
        }
    }

}
