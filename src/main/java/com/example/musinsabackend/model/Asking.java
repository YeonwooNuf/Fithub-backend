package com.example.musinsabackend.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Asking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 문의 ID

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 문의한 사용자

    @Column(nullable = false)
    private String title; // 문의 제목

    @Column(nullable = false)
    private String content; // 문의 내용

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt; // 문의 생성 날짜

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt; // 문의 업데이트 날짜

    @Enumerated(EnumType.STRING)
    private AskingStatus status; // 문의 상태 (PENDING, ANSWERED 등)

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public AskingStatus getStatus() {
        return status;
    }

    public void setStatus(AskingStatus status) {
        this.status = status;
    }
}
