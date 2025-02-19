package com.example.musinsabackend.model.user;

import com.example.musinsabackend.model.*;
import com.example.musinsabackend.model.coupon.UserCoupon;
import com.example.musinsabackend.model.point.Point;
import jakarta.persistence.*;
import java.util.List;

@Entity
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

    // Getter와 Setter

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBirthdate() {
        return birthdate;
    }
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public List<UserCoupon> getUserCoupons() {
        return userCoupons;
    }
    public void setUserCoupons(List<UserCoupon> userCoupons) {
        this.userCoupons = userCoupons;
    }

    public List<Point> getPoints() {
        return points;
    }
    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public List<Order> getOrders() {
        return orders;
    }
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Review> getReviews() {
        return reviews;
    }
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Asking> getAskings() {
        return askings;
    }
    public void setAskings(List<Asking> askings) {
        this.askings = askings;
    }

    public List<Address> getAddresses() { // ✅ 주소 Getter 추가
        return addresses;
    }
    public void setAddresses(List<Address> addresses) { // ✅ 주소 Setter 추가
        this.addresses = addresses;
    }
}
