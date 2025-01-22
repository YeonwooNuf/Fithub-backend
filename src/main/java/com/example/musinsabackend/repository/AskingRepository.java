package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.Asking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AskingRepository extends JpaRepository<Asking, Long> {
    List<Asking> findByUserUsername(String username); // username으로 검색
}
