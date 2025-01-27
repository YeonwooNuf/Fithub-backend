package com.example.musinsabackend.model;

import jakarta.persistence.*;

@Entity
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 좋아요 ID

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityPost post; // 게시글 참조

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 좋아요 누른 사용자

    // Getter와 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CommunityPost getPost() {
        return post;
    }

    public void setPost(CommunityPost post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
