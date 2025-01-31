package com.example.musinsabackend.dto;

import java.time.LocalDate;

public class CouponDto {
    private Long id;
    private String name;
    private int discount;
    private LocalDate expiryDate; // ✅ 변경: String → LocalDateTime
    private boolean isUsed;

    public CouponDto() {}

    public CouponDto(Long id, String name, int discount, LocalDate expiryDate, boolean isUsed) {
        this.id = id;
        this.name = name;
        this.discount = discount;
        this.expiryDate = expiryDate;
        this.isUsed = isUsed;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getDiscount() { return discount; }
    public void setDiscount(int discount) { this.discount = discount; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public boolean isUsed() { return isUsed; }
    public void setUsed(boolean used) { isUsed = used; }
}
