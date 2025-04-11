package com.example.musinsabackend.repository.community;

import com.example.musinsabackend.model.community.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByCommunityPost_IdAndUser_UserId(Long postId, Long userId);

    boolean existsByCommunityPost_IdAndUser_UserId(Long postId, Long userId);

    int countByCommunityPost_Id(Long postId);
}
