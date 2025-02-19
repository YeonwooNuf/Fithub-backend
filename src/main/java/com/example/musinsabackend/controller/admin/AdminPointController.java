package com.example.musinsabackend.controller.admin;

import com.example.musinsabackend.service.PointService;
import com.example.musinsabackend.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/events/points")
@RequiredArgsConstructor
@Slf4j
public class AdminPointController {

    private final PointService pointService;
    private final JwtTokenProvider jwtTokenProvider;

    // 이벤트를 통한 포인트 지급 설정 (관리자만 접근 가능)
//    @PostMapping("/register")
//    public ResponseEntity<?> registerEventPoints(@RequestHeader("Authorization") String token,
//                                                 @RequestParam Long eventId,
//                                                 @RequestParam int amount) {
//        try {
//            token = token.replace("Bearer ", "");
//            Long adminId = jwtTokenProvider.getUserIdFromToken(token);
//            log.info("✅ 관리자 ID: {} 가 이벤트 포인트 지급을 설정합니다.", adminId);
//
//            pointService.registerEventPoints(eventId, amount);
//            return ResponseEntity.ok("이벤트 포인트 지급이 설정되었습니다.");
//        } catch (Exception e) {
//            log.error("❌ 이벤트 포인트 지급 설정 중 오류 발생: ", e);
//            return ResponseEntity.status(500).body("이벤트 포인트 지급 설정 중 문제가 발생했습니다.");
//        }
//    }
}