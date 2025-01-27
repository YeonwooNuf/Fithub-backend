package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.OrderDto;
import com.example.musinsabackend.model.OrderStatus;
import com.example.musinsabackend.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 주문 생성
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDto orderDto, @RequestHeader("Authorization") String token) {
        try {
            orderService.createOrder(orderDto, token);
            return ResponseEntity.ok().body("주문이 성공적으로 생성되었습니다.");
        } catch (Exception e) {
            log.error("주문 생성 중 오류 발생: ", e);
            return ResponseEntity.status(500).body("주문 생성 중 문제가 발생했습니다.");
        }
    }

    // 사용자 주문 내역 조회
    @GetMapping
    public ResponseEntity<List<OrderDto>> getUserOrders(@RequestHeader("Authorization") String token) {
        try {
            List<OrderDto> orders = orderService.getUserOrders(token);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("주문 조회 중 오류 발생: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    // 주문 상태 변경 (관리자용)
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        try {
            // String -> OrderStatus 변환
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            orderService.updateOrderStatus(orderId, orderStatus);
            return ResponseEntity.ok().body("주문 상태가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            log.error("잘못된 주문 상태 입력: ", e);
            return ResponseEntity.badRequest().body("잘못된 주문 상태입니다.");
        } catch (Exception e) {
            log.error("주문 상태 변경 중 오류 발생: ", e);
            return ResponseEntity.status(500).body("주문 상태 변경 중 문제가 발생했습니다.");
        }
    }
}
