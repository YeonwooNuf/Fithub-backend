package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.OrderDto;
import com.example.musinsabackend.dto.OrderRequestDto;
import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        OrderDto orderDto = orderService.getOrderDetail(orderId, userId);
        return ResponseEntity.ok(orderDto);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getOrders(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<OrderDto> orders = orderService.getOrdersByUser(userId);
        return ResponseEntity.ok(orders); // ✅ 배열로 직접 응답
    }
}
