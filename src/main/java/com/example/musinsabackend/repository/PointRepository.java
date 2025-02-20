package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.point.Point;
import com.example.musinsabackend.model.point.PointStatus;
import com.example.musinsabackend.model.point.PointReason;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {


    // ✅ 특정 사용자의 포인트 내역 조회 (페이징)
    Page<Point> findByUser_UserId(Long userId, Pageable pageable);

    // ✅ 특정 사용자의 특정 상태 포인트 조회
    List<Point> findByUser_UserIdAndStatus(Long userId, PointStatus status);

    // ✅ 특정 사용자의 특정 적립 사유 포인트 조회
    List<Point> findByUser_UserIdAndReason(Long userId, PointReason reason);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Point p WHERE p.user.userId = :userId AND p.status = 'EARNED'")
    Optional<Integer> findTotalEarnedPoints(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Point p WHERE p.user.userId = :userId AND p.status = 'USED'")
    Optional<Integer> findTotalUsedPoints(@Param("userId") Long userId);


    // ✅ 만료 대상 포인트 조회 (적립된 상태에서 기간 만료된 포인트)
    @Query("SELECT p FROM Point p WHERE p.expiredAt <= :now AND p.status = 'EARNED'")
    List<Point> findByExpiredAtBeforeAndStatus(LocalDateTime now, PointStatus status);
}
