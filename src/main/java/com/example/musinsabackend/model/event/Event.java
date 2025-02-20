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
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 이벤트 제목

    @Column(columnDefinition = "TEXT", nullable = false)
    private String mainContent; // 본 내용

    @Column(columnDefinition = "TEXT")
    private String additionalContent; // 부가 내용 (추가적인 안내)

    private String imageUrl; // 첨부할 이미지 URL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType; // 이벤트 타입 (쿠폰 지급 / 적립금 지급)

    private String couponCode; // 쿠폰 지급 이벤트의 경우 쿠폰 코드 저장

    private Integer rewardPoint; // 적립금 이벤트일 경우 지급할 포인트

    private LocalDateTime createdAt; // 이벤트 생성 시간

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
