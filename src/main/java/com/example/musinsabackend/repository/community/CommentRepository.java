package com.example.musinsabackend.repository.community;

import com.example.musinsabackend.model.community.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 게시글 ID로 해당 게시글의 모든 댓글 조회 (정렬은 필요에 따라 추가)
    List<Comment> findByCommunityPost_Id(Long communityPostId);
}
