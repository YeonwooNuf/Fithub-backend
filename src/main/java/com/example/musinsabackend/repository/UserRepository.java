package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
