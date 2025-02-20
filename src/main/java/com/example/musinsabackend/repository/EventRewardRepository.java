package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.event.EventReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRewardRepository extends JpaRepository<EventReward, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
}
