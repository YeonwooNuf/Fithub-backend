package com.example.musinsabackend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDto {
    private List<CartItemDto> cartItems;  // 장바구니 상품 목록
    private int totalPrice;  // 총 가격 (쿠폰/포인트 적용 전)
    private List<CouponDto> availableCoupons;  // 사용 가능한 쿠폰 목록
    private PointDto availablePoints;  // 사용 가능한 포인트 정보
}
