package com.example.musinsabackend.controller.user;

import com.example.musinsabackend.dto.EventDto;
import com.example.musinsabackend.dto.EventRewardDto;
import com.example.musinsabackend.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    // 📌 전체 이벤트 목록 조회 (사용자)
    @GetMapping
    public ResponseEntity<List<EventDto>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    // 📌 특정 이벤트 상세 조회 (사용자)
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEventById(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEventById(eventId));
    }

    // 📌 적립금 이벤트 참여 (사용자가 버튼 클릭) - JWT에서 userId 가져오기
    @PostMapping("/{eventId}/claim")
    public ResponseEntity<EventRewardDto> claimEventReward(@PathVariable Long eventId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId"); // JWT에서 가져온 사용자 ID
        if (userId == null) {
            return ResponseEntity.status(403).body(null);
        }

        EventRewardDto eventRewardDto = new EventRewardDto(userId, eventId, null);
        return ResponseEntity.ok(eventService.claimReward(eventRewardDto));
    }
}
