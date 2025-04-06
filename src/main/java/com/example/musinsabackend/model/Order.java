package com.example.musinsabackend.model;

import com.example.musinsabackend.model.coupon.Coupon;
import com.example.musinsabackend.model.coupon.UserCoupon;
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

    @Column(unique = true)
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
    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 🔗 사용된 쿠폰들 (필요한 경우)
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "order_used_user_coupons",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "user_coupon_id")
    )
    private List<UserCoupon> usedCoupons = new ArrayList<>();

    // 편의 메서드
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    public void addUsedCoupon(UserCoupon coupon) {
        this.usedCoupons.add(coupon);
    }
}
