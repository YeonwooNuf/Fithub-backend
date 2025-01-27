package com.example.musinsabackend.dto;

public class OrderDto {
    private Long id;
    private Long productId; // 상품 ID 필드 추가
    private String productName;
    private int quantity;
    private String orderDate;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() { // 상품 ID getter
        return productId;
    }

    public void setProductId(Long productId) { // 상품 ID setter
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
