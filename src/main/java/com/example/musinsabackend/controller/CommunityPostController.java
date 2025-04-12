package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.community.CommunityPostDto;
import com.example.musinsabackend.service.community.CommunityPostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    // 🔼 게시글 등록
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(
            @RequestParam("content") String content,
            @RequestParam(value = "productIds", required = false) List<Long> productIds,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        CommunityPostDto savedPost = communityPostService.createPost(userId, content, productIds, images);
        return ResponseEntity.ok(savedPost);
    }

    // 📄 게시글 전체 목록 조회 (최신순)
    @GetMapping("/posts")
    public ResponseEntity<List<CommunityPostDto>> getAllPosts() {
        return ResponseEntity.ok(communityPostService.getAllPosts());
    }

    // 🔍 게시글 상세 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<CommunityPostDto> getPostDetail(@PathVariable Long postId) {
        return ResponseEntity.ok(communityPostService.getPostDetail(postId));
    }

    // ✏️ 게시글 수정
    @PutMapping("/posts/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long postId,
            @RequestParam("content") String content,
            @RequestParam(value = "productIds", required = false) List<Long> productIds,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        CommunityPostDto updated = communityPostService.updatePost(postId, userId, content, productIds, images);
        return ResponseEntity.ok(updated);
    }

    // ❌ 게시글 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        communityPostService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }
}
