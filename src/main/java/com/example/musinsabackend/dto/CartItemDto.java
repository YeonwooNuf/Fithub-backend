package com.example.musinsabackend.dto;

import com.example.musinsabackend.model.Brand;
import com.example.musinsabackend.model.CartItem;
import com.example.musinsabackend.model.Product;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage; // 대표 이미지 (첫 번째 이미지)
    private String brandName; // 브랜드명
    private String brandSubName; // 서브 브랜드명
    private String brandLogoUrl; // 브랜드 로고
    private String size;
    private String color;
    private int quantity;
    private double price;  // Product에서 가져옴

    public static CartItemDto fromEntity(CartItem cartItem) {
        Product product = cartItem.getProduct();
        Brand brand = product.getBrand();

        return CartItemDto.builder()
                .id(cartItem.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImage(product.getImages().isEmpty() ? "" : product.getImages().get(0)) // 첫 번째 이미지 가져오기
                .brandName(brand.getName())
                .brandSubName(brand.getSubName()) // 서브 브랜드명 추가
                .brandLogoUrl(brand.getLogoUrl()) // 브랜드 로고 추가
                .size(cartItem.getSize())
                .color(cartItem.getColor())
                .quantity(cartItem.getQuantity())
                .price(product.getPrice())  // 가격을 Product에서 가져오기
                .build();
    }
}
