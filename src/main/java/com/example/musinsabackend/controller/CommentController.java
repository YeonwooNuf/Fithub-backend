package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.community.CommentDto;
import com.example.musinsabackend.service.community.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long postId,
            @RequestBody Map<String, String> requestBody,
            @RequestParam(value = "parentId", required = false) Long parentId,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        String content = requestBody.get("content");
        return ResponseEntity.ok(commentService.addComment(userId, postId, content, parentId));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
