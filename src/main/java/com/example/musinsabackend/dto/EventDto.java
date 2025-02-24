package com.example.musinsabackend.dto;

import com.example.musinsabackend.model.event.EventType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {
    private Long id;                  // 이벤트 ID (등록 시 null, 조회 시 포함)
    private String title;             // 이벤트 제목
    private String mainContent;       // 본 내용
    private String additionalContent; // 부가 내용
    private String imageUrl;          // 이미지 URL
    private EventType eventType;      // 이벤트 타입 (쿠폰 지급 / 적립금 지급)
    private String couponCode;        // 쿠폰 코드 (쿠폰 이벤트일 경우)
    private Integer rewardPoint;      // 적립금 지급액 (적립금 이벤트일 경우)
    private LocalDate startDate;  // 이벤트 시작 날짜(생성)
    private LocalDate endDate;  // 이벤트 종료 날짜
}
