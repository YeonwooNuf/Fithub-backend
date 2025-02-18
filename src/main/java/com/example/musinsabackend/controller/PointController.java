package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.PointDto;
import com.example.musinsabackend.jwt.JwtTokenProvider;
import com.example.musinsabackend.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/points")
@CrossOrigin(origins = "http://localhost:3000")
public class PointController {

    @Autowired
    private PointService pointService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // ✅ 포인트 내역 조회
    @GetMapping("/history")
    public ResponseEntity<?> getUserPointHistory(@RequestHeader("Authorization") String token) {
        List<PointDto> pointHistory = pointService.getUserPointHistory(token);

        // 🔥 올바른 JSON 형태로 변환해서 반환
        return ResponseEntity.ok(Map.of(
                "success", true,
                "points", pointHistory  // ✅ List<PointDto>를 직접 반환해야 함
        ));
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getUserPointBalance(@RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        int totalPoints = pointService.getUserTotalPoints(userId); // ✅ 총 적립금 계산

        return ResponseEntity.ok(Map.of(
                "success", true,
                "points", totalPoints // ✅ 전체 적립금 잔액 반환
        ));
    }

    // ✅ 포인트 적립
    @PostMapping("/add")
    public ResponseEntity<?> addPoints(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        String userIdStr = String.valueOf(userId);

        int amount = (int) request.get("amount");
        String description = (String) request.get("description");

        pointService.addPoints(userId, amount, description);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "포인트가 적립되었습니다."
        ));
    }

    // ✅ 포인트 사용
    @PostMapping("/use")
    public ResponseEntity<?> usePoints(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        String userIdStr = String.valueOf(userId);

        int amount = (int) request.get("amount");

        pointService.usePoints(userId, amount);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "포인트가 사용되었습니다."
        ));
    }
}
