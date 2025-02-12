package com.example.musinsabackend.dto;

public class LikeDto {
    private Long userId;
    private Long productId;

    public LikeDto() {}

    public LikeDto(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}