package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.CommunityPostDto;
import com.example.musinsabackend.service.CommunityPostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class CommunityPostController {

    @Autowired
    private CommunityPostService communityPostService;

    // 게시글 생성
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody CommunityPostDto postDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            CommunityPostDto createdPost = communityPostService.createPost(username, postDto);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "게시글이 성공적으로 생성되었습니다.",
                    "post", createdPost
            ));
        } catch (IllegalArgumentException e) {
            log.error("게시글 생성 오류: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("게시글 생성 중 서버 오류 발생: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "게시글 생성 중 문제가 발생하였습니다."
            ));
        }
    }

    // 게시글 전체 조회
    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        try {
            List<CommunityPostDto> posts = communityPostService.getAllPosts();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "posts", posts
            ));
        } catch (Exception e) {
            log.error("게시글 조회 중 서버 오류 발생: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "게시글 조회 중 문제가 발생하였습니다."
            ));
        }
    }

    // 특정 게시글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable Long postId) {
        try {
            CommunityPostDto post = communityPostService.getPostById(postId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "post", post
            ));
        } catch (IllegalArgumentException e) {
            log.error("게시글 조회 오류: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("게시글 조회 중 서버 오류 발생: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "게시글 조회 중 문제가 발생하였습니다."
            ));
        }
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long postId,
            @RequestBody CommunityPostDto updatedPostDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            CommunityPostDto updatedPost = communityPostService.updatePost(username, postId, updatedPostDto);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "게시글이 성공적으로 수정되었습니다.",
                    "post", updatedPost
            ));
        } catch (IllegalArgumentException e) {
            log.error("게시글 수정 오류: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("게시글 수정 중 서버 오류 발생: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "게시글 수정 중 문제가 발생하였습니다."
            ));
        }
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            communityPostService.deletePost(username, postId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "게시글이 성공적으로 삭제되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            log.error("게시글 삭제 오류: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("게시글 삭제 중 서버 오류 발생: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "게시글 삭제 중 문제가 발생하였습니다."
            ));
        }
    }

    // 게시글 좋아요
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            communityPostService.likePost(username, postId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "게시글에 좋아요를 눌렀습니다."
            ));
        } catch (IllegalArgumentException e) {
            log.error("좋아요 처리 오류: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("좋아요 처리 중 서버 오류 발생: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "좋아요 처리 중 문제가 발생하였습니다."
            ));
        }
    }

    // 게시글 좋아요 취소
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            communityPostService.unlikePost(username, postId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "게시글에 대한 좋아요를 취소했습니다."
            ));
        } catch (IllegalArgumentException e) {
            log.error("좋아요 취소 오류: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("좋아요 취소 중 서버 오류 발생: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "좋아요 취소 중 문제가 발생하였습니다."
            ));
        }
    }
}
