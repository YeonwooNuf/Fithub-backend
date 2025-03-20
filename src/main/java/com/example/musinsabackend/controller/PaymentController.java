package com.example.musinsabackend.controller;

import com.example.musinsabackend.model.Payment;
import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.repository.PaymentRepository;
import com.example.musinsabackend.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final String PORTONE_API_URL = "https://api.portone.io/payments/";
    private final String PORTONE_TOKEN_URL = "https://api.portone.io/login/api-secret";
    private final String API_KEY = "8046112462071686";
    private final String API_SECRET = "6yn7EMHsbsHDEK5bNFGvhGUhMEg3b1LobW8AbUhfHVUdEZRdpyt7a5ehe4vWH5Z5240Pg27IJIjGiS5x";

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public PaymentController(PaymentRepository paymentRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @PostMapping("/complete")
    public ResponseEntity<?> completePayment(@RequestBody Map<String, Object> request) {
        try {
            String paymentId = (String) request.get("paymentId");
            Integer usedPoints = (Integer) request.get("usedPoints");

            if (paymentId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "❌ paymentId가 필요합니다."));
            }

            // ✅ usedCoupons JSON 변환
            String usedCouponsJson = new ObjectMapper().writeValueAsString(request.get("usedCoupons"));
            logger.info("🔍 결제 검증 요청: paymentId={}, usedPoints={}, usedCoupons={}", paymentId, usedPoints, usedCouponsJson);

            // ✅ PortOne API에서 결제 정보 검증
            String token = getPortOneAccessToken();
            JsonNode paymentInfo = validatePayment(paymentId);
            if (paymentInfo == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "❌ 결제 검증 실패"));
            }

            // ✅ 결제 정보 추출
            Double totalAmount = paymentInfo.get("amount").get("paid").asDouble(); // ✅ 결제 전 가격 (할인 전)
            Double finalAmount = totalAmount - usedPoints;

            Integer earnedPoints = (int) (finalAmount * 0.01);

            // ✅ 사용자 정보 조회
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                logger.warn("❌ 사용자 인증 실패 - 현재 사용자 정보를 가져올 수 없습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "❌ 사용자 인증 실패"));
            }

            logger.info("✅ 결제한 사용자 정보: id={}, username={}", currentUser.getUserId(), currentUser.getUsername());

            // ✅ 결제 정보 저장
            savePayment(paymentId, totalAmount, finalAmount, usedPoints, earnedPoints, usedCouponsJson, currentUser);

            // ✅ JSON 응답 반환
            Map<String, Object> response = new HashMap<>();
            response.put("message", "✅ 결제 검증 완료 및 저장됨");
            response.put("paymentId", paymentId);
            response.put("usedPoints", usedPoints);
            response.put("finalAmount", finalAmount);
            response.put("earnedPoints", earnedPoints);
            response.put("usedCoupons", request.get("usedCoupons"));

            return ResponseEntity.ok(response); // ✅ JSON 응답 반환
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "❌ 서버 오류 발생");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    private String getPortOneAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        // ✅ 올바른 인증 방식 적용 (Content-Type 설정)
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ✅ API Secret을 JSON 형식으로 Body에 포함
        String requestBody = "{\"apiSecret\": \"" + API_SECRET + "\"}";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            // ✅ POST 요청으로 변경
            ResponseEntity<String> response = restTemplate.exchange(
                    PORTONE_TOKEN_URL,
                    HttpMethod.POST,  // 🔥 기존 GET -> POST로 변경
                    entity,
                    String.class
            );

            // 🔥 응답 로깅
            logger.info("🔥 PortOne API 응답: {}", response.getBody());

            // ✅ 응답 JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("❌ PortOne Access Token 요청 실패: " + response.getStatusCode());
            }

            JsonNode accessTokenNode = jsonResponse.get("accessToken");
            if (accessTokenNode == null) {
                throw new RuntimeException("❌ PortOne 응답에 accessToken 필드가 없습니다: " + jsonResponse);
            }

            String token = accessTokenNode.asText();
            logger.info("✅ PortOne Access Token 발급 성공: {}", token);

            return token;
        } catch (Exception e) {
            logger.error("❌ PortOne Access Token 요청 실패", e);
            throw new RuntimeException("❌ PortOne Access Token 요청 실패: " + e.getMessage());
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

    private JsonNode validatePayment(String paymentId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "PortOne " + API_SECRET);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    PORTONE_API_URL + paymentId,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            JsonNode jsonResponse = new ObjectMapper().readTree(response.getBody());
            logger.info("✅ PortOne 응답 데이터: {}", jsonResponse);  // 🔥 전체 응답 JSON 로깅

            // ✅ 상태값 확인 (값이 존재하는지 체크)
            if (jsonResponse == null || !jsonResponse.has("status")) {
                logger.warn("❌ PortOne 응답에서 'status' 필드를 찾을 수 없음: {}", jsonResponse);
                return null;
            }

            String paymentStatus = jsonResponse.get("status").asText();
            logger.info("✅ PortOne 결제 상태: {}", paymentStatus);  // 🔥 상태 값 로깅

            if ("PAID".equalsIgnoreCase(jsonResponse.get("status").asText())) {  // ✅ 대소문자 구분 없이 비교
                return jsonResponse;
            }
            logger.warn("❌ 결제 상태가 'PAID'가 아님: {}", paymentStatus);
            return null;
        } catch (Exception e) {
            logger.error("❌ PortOne 결제 검증 실패: {}", e.getMessage());
            return null;
        }
    }

    private void savePayment(String paymentId, Double amount, Double finalAmount, Integer usedPoints,
                             Integer earnedPoints, String usedCouponsJson, User currentUser) {
        Payment payment = new Payment(paymentId, amount, finalAmount, usedPoints, earnedPoints, usedCouponsJson, "PAID", currentUser);
        paymentRepository.save(payment);
        logger.info("✅ 결제 정보 저장 완료: paymentId={}", paymentId);
    }
}
