package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.PointDto;
import com.example.musinsabackend.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/points")
public class PointController {

    @Autowired
    private PointService pointService;

    // 포인트 내역 조회
    @GetMapping
    public ResponseEntity<?> getUserPointHistory(@RequestHeader("Authorization") String token) {
        List<PointDto> pointHistory = pointService.getUserPointHistory(token);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "points", pointHistory
        ));
    }

    // 포인트 적립
    @PostMapping("/add")
    public ResponseEntity<?> addPoints(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        int amount = (int) request.get("amount");
        String description = (String) request.get("description");

        pointService.addPoints(token, amount, description);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "포인트가 적립되었습니다."
        ));
    }

    // 포인트 사용
    @PostMapping("/use")
    public ResponseEntity<?> usePoints(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        int amount = (int) request.get("amount");

        pointService.usePoints(token, amount);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "포인트가 사용되었습니다."
        ));
    }
}
