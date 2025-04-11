package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.community.PostLikeDto;
import com.example.musinsabackend.service.community.PostLikeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService likeService;

    // ❤️ 좋아요 토글
    @PostMapping("/{postId}/like")
    public ResponseEntity<PostLikeDto> toggleLike(
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(likeService.toggleLike(postId, userId));
    }

    // 👍 좋아요 개수 조회
    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<Integer> getLikeCount(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.countLikes(postId));
    }

    // ✅ 해당 유저가 좋아요 했는지 여부 조회
    @GetMapping("/{postId}/likes/me")
    public ResponseEntity<Boolean> checkMyLike(
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseEntity.ok(likeService.isLikedByUser(postId, userId));
    }
}
