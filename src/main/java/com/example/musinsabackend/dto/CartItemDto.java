package com.example.musinsabackend.dto;

import com.example.musinsabackend.model.CartItem;
import com.example.musinsabackend.model.Product;

public class CartItemDto {
    private Long id;                  // ✅ 장바구니 항목 ID
    private Long productId;           // ✅ 상품 ID
    private String productName;       // ✅ 상품 이름
    private Double productPrice;      // ✅ 상품 가격
    private String productImage;      // ✅ 대표 이미지 (첫 번째 이미지)
    private String selectedSize;      // ✅ 선택한 사이즈
    private String selectedColor;     // ✅ 선택한 색상
    private int quantity;             // ✅ 수량

    // ✅ 기본 생성자
    public CartItemDto() {}

    // ✅ 전체 필드 초기화 생성자
    public CartItemDto(Long id, Long productId, String productName, Double productPrice,
                       String productImage, String selectedSize, String selectedColor, int quantity) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImage = productImage;  // ✅ 대표 이미지 추가
        this.selectedSize = selectedSize;
        this.selectedColor = selectedColor;
        this.quantity = quantity;
    }

    // ✅ 엔티티 → DTO 변환 메서드
    public static CartItemDto fromEntity(CartItem cartItem) {
        Product product = cartItem.getProduct();
        String representativeImage = product.getImages() != null && !product.getImages().isEmpty()
                ? product.getImages().get(0)  // ✅ 첫 번째 이미지를 대표 이미지로 사용
                : "/uploads/default-image.jpg"; // ✅ 이미지가 없을 경우 기본 이미지 표시

        return new CartItemDto(
                cartItem.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                representativeImage,               // ✅ 대표 이미지 저장
                cartItem.getSelectedSize(),
                cartItem.getSelectedColor(),
                cartItem.getQuantity()
        );
    }

    // ✅ Getter & Setter
    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Double getProductPrice() { return productPrice; }
    public String getProductImage() { return productImage; }  // ✅ 대표 이미지 Getter
    public String getSelectedSize() { return selectedSize; }
    public String getSelectedColor() { return selectedColor; }
    public int getQuantity() { return quantity; }

    public void setId(Long id) { this.id = id; }
    public void setProductId(Long productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setProductPrice(Double productPrice) { this.productPrice = productPrice; }
    public void setProductImage(String productImage) { this.productImage = productImage; }  // ✅ Setter
    public void setSelectedSize(String selectedSize) { this.selectedSize = selectedSize; }
    public void setSelectedColor(String selectedColor) { this.selectedColor = selectedColor; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
