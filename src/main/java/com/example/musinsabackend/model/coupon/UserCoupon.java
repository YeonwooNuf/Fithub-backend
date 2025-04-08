package com.example.musinsabackend.model.coupon;

import com.example.musinsabackend.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
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
    private LocalDate expiryDate; // 만료일

    // ✅ 발급 시 issuedDate 자동 설정
    @PrePersist
    protected void onCreate() {
        if (issuedDate == null) {
            issuedDate = LocalDate.now();
        }
    }

    // ✅ JPA에서 중복 인식 문제 해결을 위한 equals & hashCode 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCoupon)) return false;
        UserCoupon that = (UserCoupon) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
