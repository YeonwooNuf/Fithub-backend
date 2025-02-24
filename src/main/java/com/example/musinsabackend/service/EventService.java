package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.EventDto;
import com.example.musinsabackend.dto.EventRewardDto;
import com.example.musinsabackend.model.event.Event;
import com.example.musinsabackend.model.event.EventReward;
import com.example.musinsabackend.model.event.EventType;
import com.example.musinsabackend.model.point.Point;
import com.example.musinsabackend.model.point.PointStatus;
import com.example.musinsabackend.model.point.PointReason;
import com.example.musinsabackend.model.user.User;
import com.example.musinsabackend.repository.EventRepository;
import com.example.musinsabackend.repository.EventRewardRepository;
import com.example.musinsabackend.repository.PointRepository;
import com.example.musinsabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventRewardRepository eventRewardRepository;
    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    // 📌 이벤트 등록
    public EventDto createEvent(EventDto eventDto) {
        Event event = Event.builder()
                .title(eventDto.getTitle())
                .mainContent(eventDto.getMainContent())
                .additionalContent(eventDto.getAdditionalContent())
                .imageUrl(eventDto.getImageUrl())
                .eventType(eventDto.getEventType())
                .couponCode(eventDto.getCouponCode())
                .rewardPoint(eventDto.getRewardPoint())
                .startDate(eventDto.getStartDate())
                .endDate(eventDto.getEndDate())
                .build();

        eventRepository.save(event);
        return new EventDto(event.getId(), event.getTitle(), event.getMainContent(),
                event.getAdditionalContent(), event.getImageUrl(), event.getEventType(),
                event.getCouponCode(), event.getRewardPoint(), event.getStartDate(), event.getEndDate());
    }

    // 📌 이벤트 수정
    public EventDto updateEvent(Long eventId, EventDto eventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("이벤트를 찾을 수 없습니다."));

        event.setTitle(eventDto.getTitle());
        event.setMainContent(eventDto.getMainContent());
        event.setAdditionalContent(eventDto.getAdditionalContent());
        event.setImageUrl(eventDto.getImageUrl());
        event.setEventType(eventDto.getEventType());
        event.setCouponCode(eventDto.getCouponCode());
        event.setRewardPoint(eventDto.getRewardPoint());
        event.setStartDate(eventDto.getStartDate());
        event.setEndDate(eventDto.getEndDate());

        eventRepository.save(event);

        return new EventDto(event.getId(), event.getTitle(), event.getMainContent(),
                event.getAdditionalContent(), event.getImageUrl(), event.getEventType(),
                event.getCouponCode(), event.getRewardPoint(), event.getStartDate(), event.getEndDate());
    }

    // 📌 전체 이벤트 목록 조회
    public List<EventDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(event -> new EventDto(event.getId(), event.getTitle(), event.getMainContent(),
                        event.getAdditionalContent(), event.getImageUrl(), event.getEventType(),
                        event.getCouponCode(), event.getRewardPoint(), event.getStartDate(), event.getEndDate()))
                .collect(Collectors.toList());
    }

    // 📌 특정 이벤트 조회
    public EventDto getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("이벤트를 찾을 수 없습니다."));
        return new EventDto(event.getId(), event.getTitle(), event.getMainContent(),
                event.getAdditionalContent(), event.getImageUrl(), event.getEventType(),
                event.getCouponCode(), event.getRewardPoint(), event.getStartDate(), event.getEndDate());
    }

    // 📌 이벤트 삭제
    public void deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new RuntimeException("이벤트를 찾을 수 없습니다.");
        }
        eventRepository.deleteById(eventId);
    }

    // 📌 적립금 이벤트 참여 (사용자가 버튼 클릭 시)
    public EventRewardDto claimReward(EventRewardDto eventRewardDto) {
        Long eventId = eventRewardDto.getEventId();
        Long userId = eventRewardDto.getUserId();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("이벤트를 찾을 수 없습니다."));

        if (event.getEventType() != EventType.POINT) {
            throw new RuntimeException("이 이벤트는 적립금 지급 이벤트가 아닙니다.");
        }

        // ✅ 사용자 정보 가져오기 (User 객체로 변환)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // ✅ 이미 참여한 사용자인지 확인 (userId → User 객체)
        boolean alreadyClaimed = eventRewardRepository.findByEventAndUser(event, user).isPresent();

        System.out.println("🔍 이벤트 참여 여부 확인: " + alreadyClaimed);
        System.out.println("🔍 이벤트 ID: " + event.getId());
        System.out.println("🔍 사용자 ID: " + user.getUserId());
        System.out.println("🔍 저장된 EventReward 개수: " + eventRewardRepository.count());

        if (alreadyClaimed) {
            throw new RuntimeException("이미 적립금을 받은 이벤트입니다.");
        }

        // ✅ 적립금 지급
        Point point = new Point();
        point.setUser(user);
        point.setAmount(event.getRewardPoint());
        point.setStatus(PointStatus.EARNED);
        point.setReason(PointReason.EVENT_REWARD);
        point.setCreatedAt(LocalDateTime.now());

        pointRepository.save(point);

        // ✅ 이벤트 참여 기록 저장
        EventReward eventReward = new EventReward();
        eventReward.setUser(user); // ✅ 이제 user를 직접 저장 가능!
        eventReward.setEvent(event);
        eventReward.setReceivedAt(LocalDateTime.now());

        System.out.println("🔍 이벤트 참여 기록 저장 전: userId=" + userId + ", eventId=" + eventId);
        eventRewardRepository.save(eventReward);
        System.out.println("✅ 이벤트 참여 기록 저장 완료!");

        return new EventRewardDto(userId, eventId, eventReward.getReceivedAt());
    }
}
