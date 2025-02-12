package com.example.musinsabackend.controller.user;

import com.example.musinsabackend.dto.LikeDto;
import com.example.musinsabackend.service.user.LikeService;
import com.example.musinsabackend.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
@CrossOrigin(origins = "http://localhost:3000")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleLike(@RequestBody LikeDto likeDto) {
        // ✅ 좋아요 상태를 토글하고 결과를 반환
        Map<String, Object> likeStatus = likeService.toggleLike(likeDto);
        return ResponseEntity.ok(likeStatus);
    }
}
