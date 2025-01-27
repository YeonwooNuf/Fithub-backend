package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findByUser_Username(String username);
}
