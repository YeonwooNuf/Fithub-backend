package com.example.musinsabackend.model;

import com.example.musinsabackend.model.coupon.Coupon;
import com.example.musinsabackend.model.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentId;

    private int totalAmount;

    private int finalAmount;

    private int usedPoints;

    private LocalDateTime orderDate;

    // 🔗 주문자 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 🔗 배송지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    // 🔗 주문에 포함된 상품들
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 🔗 사용된 쿠폰들 (필요한 경우)
    @ManyToMany
    @JoinTable(
            name = "order_used_coupons",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "coupon_id")
    )
    private List<Coupon> usedCoupons = new ArrayList<>();
}
