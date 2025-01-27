package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.AskingDto;
import com.example.musinsabackend.service.AskingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/askings")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class AskingController {

    @Autowired
    private AskingService askingService;

    // 문의 생성
    @PostMapping
    public ResponseEntity<?> createAsking(
            @RequestHeader("Authorization") String token,
            @RequestBody AskingDto askingDto) {
        try {
            String username = "토큰에서 추출된 사용자"; // 토큰에서 username을 추출하는 로직
            AskingDto createdAsking = askingService.createAsking(username, askingDto);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "문의가 성공적으로 생성되었습니다.",
                    "asking", createdAsking
            ));
        } catch (Exception e) {
            log.error("문의 생성 중 오류 발생: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // 사용자별 문의 조회
    @GetMapping
    public ResponseEntity<?> getUserAskings(@RequestHeader("Authorization") String token) {
        try {
            String username = "토큰에서 추출된 사용자"; // 토큰에서 username을 추출하는 로직
            List<AskingDto> askings = askingService.getUserAskings(username);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "askings", askings
            ));
        } catch (Exception e) {
            log.error("문의 조회 중 오류 발생: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
