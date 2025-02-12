package com.example.musinsabackend.controller.user;

import com.example.musinsabackend.dto.LikeDto;
import com.example.musinsabackend.service.user.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@CrossOrigin(origins = "http://localhost:3000")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/toggle")
    public ResponseEntity<String> toggleLike(@RequestBody LikeDto likeDto) {
        likeService.toggleLike(likeDto);
        return ResponseEntity.ok("Like status updated successfully.");
    }
}