package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_Username(String username);
}
