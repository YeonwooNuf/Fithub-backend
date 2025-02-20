package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.event.Event;
import com.example.musinsabackend.model.event.EventReward;
import com.example.musinsabackend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EventRewardRepository extends JpaRepository<EventReward, Long> {
    Optional<EventReward> findByEventAndUser(Event event, User user); // ✅ userId 대신 User 객체로 변경
}