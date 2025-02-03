package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.Point;
import com.example.musinsabackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findByUser(User user); // ✅ userId가 아닌 User 엔티티 자체를 사용하여 조회
    List<Point> findByUser_UserId(Long userId); // ✅ userId 기반으로 적립금 내역 조회
}
