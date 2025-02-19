package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.PointDto;
import com.example.musinsabackend.model.point.Point;
import com.example.musinsabackend.model.point.PointStatus;
import com.example.musinsabackend.model.point.PointReason;
import com.example.musinsabackend.repository.PointRepository;
import com.example.musinsabackend.repository.UserRepository;
import com.example.musinsabackend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    // 특정 사용자 포인트 내역 조회 (페이징 처리 포함)
    public Page<PointDto> getUserPoints(Long userId, int page, int size) {
        Page<Point> points = pointRepository.findByUser_UserId(userId, PageRequest.of(page, size));
        return points.map(this::convertToDto);
    }

    // 사용자의 현재 보유 포인트 조회
    public int getUserPointBalance(Long userId) {
        return pointRepository.findByUser_UserIdAndStatus(userId, PointStatus.ACTIVE)
                .stream()
                .mapToInt(Point::getAmount)
                .sum();
    }

    // 포인트 적립 (일반 구매 적립)
    @Transactional
    public void earnPoints(Long userId, int amount, PointReason reason, Long orderId) {
        Point point = new Point();
        point.setUser(userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")));
        point.setAmount(amount);
        point.setStatus(PointStatus.ACTIVE);
        point.setType("EARN");
        point.setReason(reason);
        point.setCreatedAt(LocalDateTime.now());
        point.setExpiredAt(LocalDateTime.now().plusYears(1)); // 1년 후 만료
        if (orderId != null) {
            point.setOrder(orderRepository.findById(orderId).orElse(null));
        }
        pointRepository.save(point);
    }

    // 포인트 사용 (결제 시 차감)
    @Transactional
    public void usePoints(Long userId, int amount, PointReason reason, Long orderId) {
        List<Point> activePoints = pointRepository.findByUser_UserIdAndStatus(userId, PointStatus.ACTIVE);
        int totalAvailable = activePoints.stream().mapToInt(Point::getAmount).sum();

        if (totalAvailable < amount) {
            throw new IllegalArgumentException("보유 포인트가 부족합니다.");
        }

        int remainingAmount = amount;
        for (Point point : activePoints) {
            if (remainingAmount <= 0) break;

            int deduction = Math.min(point.getAmount(), remainingAmount);
            point.setAmount(point.getAmount() - deduction);
            if (point.getAmount() == 0) {
                point.setStatus(PointStatus.USED);
            }
            remainingAmount -= deduction;
            pointRepository.save(point);
        }
    }

    // 포인트 만료 처리 (스케줄링 가능)
    @Transactional
    public void expirePoints() {
        List<Point> expiringPoints = pointRepository.findByExpiredAtBeforeAndStatus(LocalDateTime.now(), PointStatus.ACTIVE);
        for (Point point : expiringPoints) {
            point.setStatus(PointStatus.EXPIRED);
            pointRepository.save(point);
        }
    }

    private PointDto convertToDto(Point point) {
        return new PointDto(
                point.getId(),
                point.getUser().getUserId(),
                point.getAmount(),
                point.getStatus(),
                point.getType(),
                point.getReason().name(),
                point.getCreatedAt(),
                point.getExpiredAt(),
                point.getOrder() != null ? point.getOrder().getId() : null
        );
    }
}
