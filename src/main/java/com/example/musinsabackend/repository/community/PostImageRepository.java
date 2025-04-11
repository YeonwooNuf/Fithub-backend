package com.example.musinsabackend.repository.community;

import com.example.musinsabackend.model.community.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    // 필요 시 특정 게시글의 이미지 목록 가져오기
    List<PostImage> findByCommunityPost_Id(Long communityPostId);
}
