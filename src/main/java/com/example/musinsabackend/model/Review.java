package com.example.musinsabackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 ID
    private Long id;

    @Column(nullable = false)
    private String title; // 리뷰 제목

    @Column(nullable = false, length = 2000)
    private String content; // 리뷰 내용

    private String reviewImageUrl; // 리뷰 이미지 URL

    @Column(nullable = false)
    private int rating; // 별점 (1~5)

    @Column(nullable = false)
    private LocalDateTime createdDate; // 작성 날짜

    @ManyToOne(fetch = FetchType.LAZY) // 리뷰와 사용자 간 N:1 관계
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // 리뷰와 상품 간 N:1 관계
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // 리뷰 대상 상품 (Product 엔티티 필요)

    // Getter와 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReviewImageUrl() {
        return reviewImageUrl;
    }

    public void setReviewImageUrl(String reviewImageUrl) {
        this.reviewImageUrl = reviewImageUrl;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
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
}
