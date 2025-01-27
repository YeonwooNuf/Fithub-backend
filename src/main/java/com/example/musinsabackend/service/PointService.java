package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.PointDto;
import com.example.musinsabackend.model.Point;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.repository.PointRepository;
import com.example.musinsabackend.repository.UserRepository;
import com.example.musinsabackend.jwt.JwtTokenProvider;
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

    // 사용자 포인트 내역 조회
    public List<PointDto> getUserPointHistory(String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        List<Point> points = pointRepository.findByUser_Username(username);

        return points.stream().map(point -> {
            PointDto dto = new PointDto();
            dto.setId(point.getId());
            dto.setDescription(point.getDescription());
            dto.setAmount(point.getAmount());
            dto.setDate(point.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return dto;
        }).collect(Collectors.toList());
    }

    // 포인트 적립
    public void addPoints(String token, int amount, String description) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Point point = new Point();
        point.setUser(user);
        point.setAmount(amount);
        point.setDescription(description);
        point.setDate(LocalDateTime.now());

        pointRepository.save(point);
    }

    // 포인트 사용
    public void usePoints(String token, int amount) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        int totalPoints = pointRepository.findByUser_Username(username)
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

    public void deductPoints(String username, int pointUsage) {
        // 사용자 포인트 기록 조회
        List<Point> points = pointRepository.findByUser_Username(username);

        if (points.isEmpty()) {
            throw new IllegalArgumentException("사용자의 포인트 기록이 존재하지 않습니다.");
        }

        // 포인트 차감 로직
        for (Point point : points) {
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

        if (pointUsage > 0) {
            throw new IllegalArgumentException("사용 가능한 포인트가 부족합니다.");
        }
    }

}
