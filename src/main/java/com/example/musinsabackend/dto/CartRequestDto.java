package com.example.musinsabackend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CartRequestDto {
    private Long productId;
    private String size;
    private String color;
    private int quantity;
}
