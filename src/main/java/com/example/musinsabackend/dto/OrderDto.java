package com.example.musinsabackend.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class OrderDto {
    private Long orderId;
    private String paymentId;
    private int totalAmount;
    private int finalAmount;
    private int usedPoints;
    private String orderDate;
    private List<OrderItemDto> items; // ✅ 외부 DTO로 분리
}
