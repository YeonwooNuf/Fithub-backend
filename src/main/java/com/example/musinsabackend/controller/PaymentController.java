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

            // ✅ usedCoupons를 JSON 문자열로 변환 (에러 해결)
            ObjectMapper objectMapper = new ObjectMapper();
            String usedCouponsJson = objectMapper.writeValueAsString(request.get("usedCoupons"));

            if (paymentId == null) {
                return ResponseEntity.badRequest().body("❌ paymentId가 필요합니다.");
            }

            logger.info("🔍 결제 검증 요청: paymentId={}, usedPoints={}, usedCoupons={}", paymentId, usedPoints, usedCouponsJson);

            // ✅ PortOne API를 사용해 결제 검증
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            String token = getPortOneAccessToken();
            logger.info("✅ PortOne API 요청에 사용될 액세스 토큰: {}", token); // 로그 추가

            headers.set("Authorization", "PortOne " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    PORTONE_API_URL + paymentId,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            logger.info("✅ PortOne 응답: {}", jsonResponse);

            if ("paid".equals(jsonResponse.get("status").asText())) {
                Double amount = jsonResponse.get("totalAmount").asDouble();
                Double finalAmount = amount - usedPoints;
                Integer earnedPoints = (int) (finalAmount * 0.01);

                User currentUser = getCurrentUser();
                if (currentUser == null) {
                    logger.warn("❌ 사용자 인증 실패 - 현재 사용자 정보를 가져올 수 없습니다.");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ 사용자 인증 실패");
                }

                logger.info("✅ 결제한 사용자 정보: id={}, username={}", currentUser.getUserId(), currentUser.getUsername());

                // ✅ Payment 객체 저장 (usedCoupons JSON 문자열 사용)
                Payment payment = new Payment(paymentId, amount, finalAmount, usedPoints, earnedPoints, usedCouponsJson, "PAID", currentUser);
                paymentRepository.save(payment);

                return ResponseEntity.ok("✅ 결제 검증 완료 및 저장됨");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ 결제 검증 실패");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ 서버 오류 발생: " + e.getMessage());
        }
    }

    private String getPortOneAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        // ✅ 올바른 인증 방식 적용 (Authorization 헤더 설정)
        headers.set("Authorization", "PortOne " + API_SECRET);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // ✅ GET 요청 방식으로 변경 (Body 없이 요청)
            ResponseEntity<String> response = restTemplate.exchange(
                    PORTONE_TOKEN_URL,
                    HttpMethod.GET, // 🔥 POST → GET으로 변경
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

            JsonNode accessTokenNode = jsonResponse.get("access_token");
            if (accessTokenNode == null) {
                throw new RuntimeException("❌ PortOne 응답에 access_token 필드가 없습니다: " + jsonResponse);
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

    private boolean validatePortOneToken(String token, String paymentId) {
        if (paymentId == null || paymentId.isEmpty()) {
            logger.error("❌ 유효하지 않은 결제 ID: paymentId 값이 없습니다.");
            return false;
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "PortOne " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // ✅ 토큰을 이용해 실제 결제 정보를 조회해봄 (GET 요청)
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.portone.io/payments/" + paymentId,  // PortOne 결제 조회 API
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            logger.info("✅ PortOne Token 검증 응답: {}", response.getStatusCode());
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.error("❌ PortOne Token 검증 실패: {}", e.getMessage());
            return false;
        }
    }

}
