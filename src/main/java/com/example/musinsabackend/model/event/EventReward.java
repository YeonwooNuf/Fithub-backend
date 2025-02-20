package com.example.musinsabackend.model.event;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "event_reward")
public class EventReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event; // 참여한 이벤트

    @Column(nullable = false)
    private Long userId; // 참여한 사용자 ID

    private LocalDateTime receivedAt; // 적립금 받은 시간

    @PrePersist
    public void prePersist() {
        this.receivedAt = LocalDateTime.now();
    }
}
