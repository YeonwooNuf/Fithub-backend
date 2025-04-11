package com.example.musinsabackend.repository.community;

import com.example.musinsabackend.model.community.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    // 사용자별 작성 게시글 조회 (마이페이지 등에서 사용 가능)
    List<CommunityPost> findByUser_UserId(Long userId);

    // 최신순 게시글 목록 조회 (홈 피드에서 사용)
    List<CommunityPost> findAllByOrderByCreatedAtDesc();

    // 키워드 검색
    List<CommunityPost> findByContentContaining(String keyword);

    // 상품에 연결된 게시글 조회
    List<CommunityPost> findByLinkedProductId(Long productId);
}
