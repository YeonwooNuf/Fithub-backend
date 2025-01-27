package com.example.musinsabackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 주문 ID

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 주문한 사용자

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // 주문한 상품

    @Column(nullable = false)
    private Integer quantity; // 구매 수량

    @Column(nullable = false)
    private Double totalPrice; // 총 가격

    @Column(nullable = false)
    private LocalDateTime orderDate; // 주문 날짜

    @Column(nullable = true)
    private String paymentKey; // 결제 키값 (외부 결제 API 연동용)

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태 (PENDING, COMPLETED, CANCELLED 등)

    // Getter와 Setter
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public void setPaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
