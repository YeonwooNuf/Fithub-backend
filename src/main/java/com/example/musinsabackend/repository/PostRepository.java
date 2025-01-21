package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUsername(String username);
}