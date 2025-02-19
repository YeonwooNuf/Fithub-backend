package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.point.Point;
import com.example.musinsabackend.model.point.PointStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {

    Page<Point> findByUser_UserId(Long userId, Pageable pageable);

    List<Point> findByUser_UserIdAndStatus(Long userId, PointStatus status);

    @Query("SELECT p FROM Point p WHERE p.expiredAt <= :now AND p.status = 'ACTIVE'")
    List<Point> findByExpiredAtBeforeAndStatus(LocalDateTime now, PointStatus status);

    boolean existsByUser_UserIdAndReason(Long userId, String reason);
}
