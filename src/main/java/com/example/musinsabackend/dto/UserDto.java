package com.example.musinsabackend.dto;

import java.util.List;

public class UserDto {
    private Long userId;
    private String username;
    private String password;

    private String nickname;
    private String birthdate;
    private String phone;
    private String gender;
    private String profileImageUrl; // 프로필 사진 URL

    // 참조 관계를 위한 필드
    private List<OrderDto> orders; // 주문 내역
    private List<ReviewDto> reviews; // 리뷰
    private List<AskingDto> askings; // 1:1 문의 내역
    private List<CommunityPostDto> communityPosts; // 커뮤니티 게시글
    private List<CouponDto> coupons; // 쿠폰 내역
    private List<PointDto> points; // 포인트 내역

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public List<OrderDto> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderDto> orders) {
        this.orders = orders;
    }

    public List<ReviewDto> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDto> reviews) {
        this.reviews = reviews;
    }

    public List<AskingDto> getAskings() {
        return askings;
    }

    public void setAskings(List<AskingDto> askings) {
        this.askings = askings;
    }

    public List<CommunityPostDto> getCommunityPosts() {
        return communityPosts;
    }

    public void setCommunityPosts(List<CommunityPostDto> communityPosts) {
        this.communityPosts = communityPosts;
    }

    public List<CouponDto> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<CouponDto> coupons) {
        this.coupons = coupons;
    }

    public List<PointDto> getPoints() {
        return points;
    }

    public void setPoints(List<PointDto> points) {
        this.points = points;
    }
}
