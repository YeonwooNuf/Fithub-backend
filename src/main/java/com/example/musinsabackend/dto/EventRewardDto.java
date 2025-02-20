package com.example.musinsabackend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRewardDto {
    private Long userId;      // 적립금을 받을 사용자 ID
    private Long eventId;     // 참여한 이벤트 ID
    private LocalDateTime receivedAt; // 적립금 지급 시간
}
