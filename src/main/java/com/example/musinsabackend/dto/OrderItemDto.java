package com.example.musinsabackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {
    private Long productId;
    private int quantity;
    private int price;
}
