package com.example.musinsabackend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDto {

    private String paymentId;

    private int totalAmount;

    private int finalAmount;

    private int usedPoints;

    private Long addressId;

    private List<Long> usedCouponIds;

    private List<OrderItemDto> items;
}
