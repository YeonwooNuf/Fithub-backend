package com.example.musinsabackend.controller;

import com.example.musinsabackend.model.Payment;
import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.repository.PaymentRepository;
import com.example.musinsabackend.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final String PORTONE_API_URL = "https://api.portone.io/payments/";
    private final String PORTONE_TOKEN_URL = "https://api.portone.io/auth/token";
    private final String API_KEY = "8046112462071686";
    private final String API_SECRET = "i1gfTdQ4LW1fseu3tW62ngmg4BhHjNYCw5Bg4BpgnHR52sfF3vaXZbfUVrX6MUZN4jUWfA8opErBrdIu";

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public PaymentController(PaymentRepository paymentRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @PostMapping("/complete")
    public ResponseEntity<?> completePayment(@RequestBody Map<String, Object> request) {
        String paymentId = (String) request.get("paymentId");
        Integer usedPoints = (Integer) request.get("usedPoints");
        String usedCoupons = (String) request.get("usedCoupons");

        if (paymentId == null) {
            return ResponseEntity.badRequest().body("❌ paymentId가 필요합니다.");
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + getPortOneAccessToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    PORTONE_API_URL + paymentId,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());

            if ("paid".equals(jsonResponse.get("status").asText())) {
                Double amount = jsonResponse.get("totalAmount").asDouble();
                Double finalAmount = amount - usedPoints;
                Integer earnedPoints = (int) (finalAmount * 0.01);

                User currentUser = getCurrentUser();
                if (currentUser == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ 사용자 인증 실패");
                }

                Payment payment = new Payment(paymentId, amount, finalAmount, usedPoints, earnedPoints, usedCoupons, "PAID", currentUser);
                paymentRepository.save(payment);

                return ResponseEntity.ok("✅ 결제 검증 완료 및 저장됨");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ 결제 검증 실패");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ 서버 오류 발생");
        }
    }

    private String getPortOneAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("{\"apiKey\": \"%s\", \"apiSecret\": \"%s\"}", API_KEY, API_SECRET);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                PORTONE_TOKEN_URL,
                HttpMethod.POST,
                entity,
                String.class
        );

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            return jsonResponse.get("accessToken").asText();
        } catch (Exception e) {
            throw new RuntimeException("❌ PortOne Access Token 요청 실패");
        }
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        }
        return null;
    }
}
