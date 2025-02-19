package com.example.musinsabackend.dto;

import com.example.musinsabackend.model.point.PointStatus;
import java.time.LocalDateTime;

public class PointDto {
    private Long id;
    private Long userId;
    private int amount;
    private PointStatus status;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private Long orderId;

    public PointDto(Long id, Long userId, int amount, PointStatus status, String reason,
                    LocalDateTime createdAt, LocalDateTime expiredAt, Long orderId) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.reason = reason;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.orderId = orderId;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public int getAmount() {
        return amount;
    }

    public PointStatus getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public Long getOrderId() {
        return orderId;
    }
}
