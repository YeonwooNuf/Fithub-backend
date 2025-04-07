package com.example.musinsabackend.controller;

import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.repository.UserRepository;
import com.example.musinsabackend.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final UserRepository userRepository;

    public PaymentController(PaymentService paymentService, UserRepository userRepository) {
        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }

    @PostMapping("/complete")
    public ResponseEntity<?> completePayment(@RequestBody Map<String, Object> request) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                logger.warn("❌ 사용자 인증 실패");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "❌ 사용자 인증 실패"));
            }

            // 서비스에 위임
            Map<String, Object> response = paymentService.completePayment(request, currentUser);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("❌ 결제 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "❌ 결제 처리 실패", "message", e.getMessage()));
        }
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            Optional<User> userOptional = userRepository.findByUsername(userDetails.getUsername());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                logger.info("✅ 현재 사용자 조회 성공: id={}, username={}", user.getUserId(), user.getUsername());
                return user;
            } else {
                logger.warn("❌ 사용자 정보 없음: username={}", userDetails.getUsername());
            }
        } else {
            logger.warn("❌ SecurityContext에서 UserDetails를 찾을 수 없음");
        }
        return null;
    }
}
