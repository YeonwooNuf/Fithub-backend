package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.PointDto;
import com.example.musinsabackend.model.point.PointReason;
import com.example.musinsabackend.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    // 사용자의 포인트 내역 조회 (페이징 지원)
    @GetMapping
    public ResponseEntity<Page<PointDto>> getUserPoints(HttpServletRequest request,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null)
            return ResponseEntity.status(401).build();

        return ResponseEntity.ok(pointService.getUserPoints(userId, page, size));
    }

    // 사용자의 현재 보유 포인트 조회
    @GetMapping("/balance")
    public ResponseEntity<Integer> getUserPointBalance(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null)
            return ResponseEntity.status(401).build();

        return ResponseEntity.ok(pointService.getUserPointBalance(userId));
    }

    // 포인트 사용 (결제 시 차감)
    @PostMapping("/use")
    public ResponseEntity<String> usePoints(HttpServletRequest request,
                                            @RequestParam int amount,
                                            @RequestParam PointReason reason,
                                            @RequestParam(required = false) Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null)
            return ResponseEntity.status(401).build();

        pointService.usePoints(userId, amount, reason, orderId);
        return ResponseEntity.ok("포인트가 사용되었습니다.");
    }
}
