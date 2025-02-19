package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.point.Point;
import com.example.musinsabackend.model.point.PointStatus;
import com.example.musinsabackend.model.point.PointReason;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {


    // ✅ 특정 사용자의 포인트 내역 조회 (페이징)
    Page<Point> findByUser_UserId(Long userId, Pageable pageable);

    // ✅ 특정 사용자의 특정 상태 포인트 조회
    List<Point> findByUser_UserIdAndStatus(Long userId, PointStatus status);

    // ✅ 특정 사용자의 특정 적립 사유 포인트 조회
    List<Point> findByUser_UserIdAndReason(Long userId, PointReason reason);

    // ✅ 사용자가 특정 사유의 포인트를 보유하고 있는지 확인(이벤트 후기 적립 중복 방지)
    boolean existsByUser_UserIdAndReason(Long userId, PointReason reason);

    // ✅ 만료 대상 포인트 조회 (적립된 상태에서 기간 만료된 포인트)
    @Query("SELECT p FROM Point p WHERE p.expiredAt <= :now AND p.status = 'EARNED'")
    List<Point> findByExpiredAtBeforeAndStatus(LocalDateTime now, PointStatus status);
}
