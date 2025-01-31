package com.example.musinsabackend.dto;

import java.time.LocalDateTime;

public class PointDto {

    private Long id;
    private String description;
    private int amount;
    private LocalDateTime date; // ✅ 변경: String → LocalDateTime

    public PointDto() {}

    public PointDto(Long id, String description, int amount, LocalDateTime date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}
