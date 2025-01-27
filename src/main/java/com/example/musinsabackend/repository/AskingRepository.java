package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.Asking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AskingRepository extends JpaRepository<Asking, Long> {
    List<Asking> findByUser_Username(String username);
}
