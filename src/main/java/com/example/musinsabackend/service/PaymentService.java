package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.OrderRequestDto;
import com.example.musinsabackend.model.Payment;
import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.repository.PaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final String PORTONE_API_URL = "https://api.portone.io/payments/";
    private final String PORTONE_TOKEN_URL = "https://api.portone.io/login/api-secret";
    private final String API_SECRET = "6yn7EMHsbsHDEK5bNFGvhGUhMEg3b1LobW8AbUhfHVUdEZRdpyt7a5ehe4vWH5Z5240Pg27IJIjGiS5x";

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    public PaymentService(PaymentRepository paymentRepository, OrderService orderService) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
    }

    @Transactional
    public Map<String, Object> completePayment(Map<String, Object> request, User currentUser) throws Exception {
        String paymentId = (String) request.get("paymentId");
        Integer usedPoints = (Integer) request.get("usedPoints");

        Number totalAmountNumber = (Number) request.get("totalAmount");
        Number finalAmountNumber = (Number) request.get("finalAmount");

        Double totalAmount = totalAmountNumber.doubleValue();
        Double finalAmount = finalAmountNumber.doubleValue();

        if (paymentId == null) {
            throw new IllegalArgumentException("paymentId가 필요합니다.");
        }

        String usedCouponsJson = new ObjectMapper().writeValueAsString(request.get("usedCoupons"));
        logger.info("🔍 결제 검증 요청: paymentId={}, usedPoints={}, usedCoupons={}", paymentId, usedPoints, usedCouponsJson);

        // PortOne 결제 상태 검증
        String token = getPortOneAccessToken();
        JsonNode paymentInfo = validatePayment(paymentId);
        if (paymentInfo == null) {
            throw new RuntimeException("결제 검증 실패");
        }

        Double paidAmount = paymentInfo.get("amount").get("paid").asDouble();
        Integer earnedPoints = (int) (paidAmount * 0.05);

        logger.info("✅ 결제한 사용자 정보: id={}, username={}", currentUser.getUserId(), currentUser.getUsername());

        // 결제 정보 저장
        Payment payment = new Payment(paymentId, totalAmount, finalAmount, usedPoints, earnedPoints, usedCouponsJson, "PAID", currentUser);
        paymentRepository.save(payment);
        logger.info("✅ 결제 정보 저장 완료: paymentId={}", paymentId);

        // 주문 정보 저장
        OrderRequestDto orderRequestDto = new ObjectMapper().convertValue(request, OrderRequestDto.class);
        orderService.saveOrder(orderRequestDto, currentUser.getUserId());
        logger.info("✅ 주문 정보 저장 완료: paymentId={}", paymentId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "✅ 결제 검증 완료 및 저장됨");
        response.put("paymentId", paymentId);
        response.put("usedPoints", usedPoints);
        response.put("finalAmount", finalAmount);
        response.put("earnedPoints", earnedPoints);
        response.put("usedCoupons", request.get("usedCoupons"));
        response.put("totalAmount", totalAmount);

        return response;
    }

    private String getPortOneAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{\"apiSecret\": \"" + API_SECRET + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    PORTONE_TOKEN_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            logger.info("🔥 PortOne API 응답: {}", response.getBody());

            JsonNode jsonResponse = new ObjectMapper().readTree(response.getBody());

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("PortOne Access Token 요청 실패: " + response.getStatusCode());
            }

            JsonNode accessTokenNode = jsonResponse.get("accessToken");
            if (accessTokenNode == null) {
                throw new RuntimeException("PortOne 응답에 accessToken 필드 없음: " + jsonResponse);
            }

            String token = accessTokenNode.asText();
            logger.info("✅ PortOne Access Token 발급 성공: {}", token);

            return token;
        } catch (Exception e) {
            logger.error("❌ PortOne Access Token 요청 실패", e);
            throw new RuntimeException("PortOne Access Token 요청 실패: " + e.getMessage());
        }
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
            logger.info("✅ PortOne 응답 데이터: {}", jsonResponse);

            if (jsonResponse == null || !jsonResponse.has("status")) {
                logger.warn("❌ PortOne 응답에 'status' 필드 없음: {}", jsonResponse);
                return null;
            }

            String paymentStatus = jsonResponse.get("status").asText();
            logger.info("✅ PortOne 결제 상태: {}", paymentStatus);

            if ("PAID".equalsIgnoreCase(paymentStatus)) {
                return jsonResponse;
            }

            logger.warn("❌ 결제 상태가 PAID 아님: {}", paymentStatus);
            return null;
        } catch (Exception e) {
            logger.error("❌ PortOne 결제 검증 실패: {}", e.getMessage());
            return null;
        }
    }
}
