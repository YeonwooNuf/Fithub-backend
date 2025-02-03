package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.PointDto;
import com.example.musinsabackend.jwt.JwtTokenProvider;
import com.example.musinsabackend.model.Point;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.repository.PointRepository;
import com.example.musinsabackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PointService {

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // ✅ 사용자 포인트 내역 조회 (userId 기반)
    public List<PointDto> getUserPointHistory(String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Point> points = pointRepository.findByUser(user); // ✅ 사용자의 포인트 내역 가져오기

        return points.stream().map(point -> new PointDto(
                point.getId(),
                point.getDescription(),
                point.getAmount(),
                point.getDate()
        )).collect(Collectors.toList()); // ✅ List<PointDto> 형태로 변환
    }


    // ✅ 포인트 적립 (userId 기반)
    public void addPoints(Long userId, int amount, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Point point = new Point();
        point.setUser(user);
        point.setAmount(amount);
        point.setDescription(description);
        point.setDate(LocalDateTime.now());

        pointRepository.save(point);
    }

    // ✅ 포인트 사용 (userId 기반)
    public void usePoints(Long userId, int amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        int totalPoints = pointRepository.findByUser(user)
                .stream()
                .mapToInt(Point::getAmount)
                .sum();

        if (totalPoints < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        Point point = new Point();
        point.setUser(user);
        point.setAmount(-amount); // 사용 시 음수 처리
        point.setDescription("포인트 사용");
        point.setDate(LocalDateTime.now());

        pointRepository.save(point);
    }

    // ✅ 포인트 차감 메소드 추가(환불, 취소 시)
    public void deductPoints(Long userId, int pointUsage) {
        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 현재 보유 포인트 합산
        int totalPoints = pointRepository.findByUser(user)
                .stream()
                .mapToInt(Point::getAmount)
                .sum();

        if (totalPoints < pointUsage) {
            throw new IllegalArgumentException("사용 가능한 포인트가 부족합니다.");
        }

        // 포인트 차감 로직
        for (Point point : pointRepository.findByUser(user)) {
            if (point.getAmount() >= pointUsage) {
                point.setAmount(point.getAmount() - pointUsage);
                pointRepository.save(point);
                return;
            } else {
                pointUsage -= point.getAmount();
                point.setAmount(0);
                pointRepository.save(point);
            }
        }
    }

    // ✅ 사용자의 총 적립금(잔액) 계산
    public int getUserTotalPoints(Long userId) {
        // 사용자의 전체 포인트 내역 조회
        List<Point> points = pointRepository.findByUser_UserId(userId);

        // 모든 포인트 amount 합산
        return points.stream()
                .mapToInt(Point::getAmount)
                .sum(); // ✅ 최종 적립금 반환
    }
}
