package com.example.musinsabackend.controller.admin;

import com.example.musinsabackend.dto.UserDto;
import com.example.musinsabackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    // ✅ 1. 전체 사용자 목록 조회 (페이지네이션 지원)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<UserDto> userPage = userService.getAllUsers(page, size);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "전체 사용자 목록을 조회했습니다.",
                "users", userPage.getContent(),
                "currentPage", userPage.getNumber(),
                "totalPages", userPage.getTotalPages(),
                "totalUsers", userPage.getTotalElements()
        ));
    }

    // ✅ 2. 특정 사용자 검색 (username 또는 nickname)
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUser(@RequestParam String query) {
        try {
            UserDto user = userService.searchUser(query);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "사용자 정보를 조회했습니다.",
                    "user", user
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // ✅ 3. 사용자 삭제 (Hard Delete)
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "사용자가 성공적으로 삭제되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
