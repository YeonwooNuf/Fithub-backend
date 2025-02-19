package com.example.musinsabackend.model.point;

import com.example.musinsabackend.model.Order;
import com.example.musinsabackend.model.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int amount; // 포인트 금액 (양수: 적립, 음수: 사용)

    @Enumerated(EnumType.STRING)
    private PointStatus status; // 포인트 상태 (ACTIVE, USED, EXPIRED)

    private String type; // 포인트 유형 (EARN, USE, EXPIRE)

    @Enumerated(EnumType.STRING)
    private PointReason reason; // ✅ ENUM 적용

    private LocalDateTime createdAt; // 포인트 발생일

    private LocalDateTime expiredAt; // 포인트 만료일

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = true)
    private Order order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public PointStatus getStatus() {
        return status;
    }

    public void setStatus(PointStatus status) {
        this.status = status;
    }

    public PointReason getReason() {
        return reason;
    }

    public void setReason(PointReason reason) {
        this.reason = reason;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
