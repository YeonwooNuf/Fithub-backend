package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    // 특정 사용자의 게시글 조회
    List<CommunityPost> findByUser_Username(String username);

    // 제목으로 게시글 검색 (예: 검색 기능 추가 시)
    List<CommunityPost> findByTitleContainingIgnoreCase(String title);
}
