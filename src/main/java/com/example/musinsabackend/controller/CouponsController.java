package com.example.musinsabackend.controller;

import com.example.musinsabackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
@Slf4j
public class CouponsController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getCoupons(
            @RequestParam String username,
            @RequestHeader("Authorization") String token) {
        log.info("Received request for coupons with username: {}", username);

        try {
            // Token validation
            boolean isValidToken = userService.validateToken(token, username);
            if (!isValidToken) {
                log.warn("Invalid token for username: {}", username);
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Invalid token"));
            }

            // Get coupon count
            int coupons = userService.getUserCoupons(username);
            log.info("Coupons for user {}: {}", username, coupons);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Coupons retrieved successfully",
                    "count", coupons
            ));

        } catch (Exception e) {
            log.error("Error fetching coupons for user {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error fetching coupons"
            ));
        }
    }
}
