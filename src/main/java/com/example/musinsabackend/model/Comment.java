package com.example.musinsabackend.model;

import jakarta.persistence.*;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 댓글 ID

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityPost post; // 게시글 참조

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 댓글 작성자

    @Column(nullable = false)
    private String content; // 댓글 내용

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
