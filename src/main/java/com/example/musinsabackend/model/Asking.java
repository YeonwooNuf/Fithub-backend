package com.example.musinsabackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Asking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "username", nullable = false)
    private User user;

    private String title;
    private String content;
    private LocalDateTime askingDate;

    private String response; // 관리자의 답변

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

    public LocalDateTime getAskingDate() {
        return askingDate;
    }

    public void setAskingDate(LocalDateTime askingDate) {
        this.askingDate = askingDate;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
