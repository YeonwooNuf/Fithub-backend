package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.AskingDto;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.service.AskingService;
import com.example.musinsabackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/askings")
public class AskingController {

    @Autowired
    private AskingService askingService;

    @Autowired
    private UserService userService;

    /**
     * 새로운 문의 등록
     * @param askingDto 문의 데이터
     * @return 성공 메시지
     */
    @PostMapping
    public ResponseEntity<?> createAsking(@RequestBody AskingDto askingDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // 현재 로그인한 사용자 가져오기
        User user = userService.findUserByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
        }

        askingService.createAsking(askingDto, user);
        return ResponseEntity.ok("문의 등록 완료");
    }

    /**
     * 현재 로그인한 사용자의 문의 내역 가져오기
     * @return 문의 내역 리스트
     */
    @GetMapping
    public ResponseEntity<List<AskingDto>> getUserAskings() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // 현재 로그인한 사용자 가져오기
        List<AskingDto> userAskings = askingService.getUserAskings(username);
        return ResponseEntity.ok(userAskings);
    }
}
