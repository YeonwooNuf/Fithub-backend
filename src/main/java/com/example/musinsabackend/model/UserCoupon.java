package com.example.musinsabackend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user; // 쿠폰을 가진 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon; // 발급된 쿠폰 정보

    @Column(nullable = false)
    private boolean isUsed = false; // 사용 여부 (기본값: false)

    @Column(nullable = false)
    private LocalDate issuedDate; // 발급일

    @Column(nullable = false)
    private LocalDate expiryDate; // 만료일 (expiresDate → expiryDate로 변경)

    // ✅ 발급 시 issuedDate 자동 설정
    @PrePersist
    protected void onCreate() {
        if (issuedDate == null) {
            issuedDate = LocalDate.now();
        }
    }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Coupon getCoupon() { return coupon; }
    public void setCoupon(Coupon coupon) { this.coupon = coupon; }

    public boolean isUsed() { return isUsed; }
    public void setUsed(boolean used) { isUsed = used; }

    public LocalDate getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDate issuedDate) { this.issuedDate = issuedDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
}
