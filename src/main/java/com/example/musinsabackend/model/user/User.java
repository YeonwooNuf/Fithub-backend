package com.example.musinsabackend.model.user;

import com.example.musinsabackend.model.*;
import com.example.musinsabackend.model.coupon.UserCoupon;
import com.example.musinsabackend.model.point.Point;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 ID
    private Long userId; // user_id 기본 키

    @Column(nullable = false, unique = true)
    private String username; // 유저네임 (회원가입 ID)

    @Column(nullable = false)
    private String password;

    private String nickname;
    private String birthdate;
    private String phone;
    private String gender;

    private String profileImageUrl; // 프로필 사진 URL

    @Enumerated(EnumType.STRING) // ✅ Enum 타입으로 저장
    @Column(nullable = false)
    private Role role = Role.USER; // ✅ 기본값을 USER로 설정

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCoupon> userCoupons;  // ✅ 쿠폰 관계

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Point> points; // 적립금 관계

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders; // 주문 내역 관계

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews; // 리뷰 관계

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asking> askings; // 문의 내역 관계

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses; // ✅ 사용자 주소 관계 추가

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider = AuthProvider.LOCAL;

    private String providerId;

    public User(Long userId) {
        this.userId = userId;
    }

    public User() {

    }
}
