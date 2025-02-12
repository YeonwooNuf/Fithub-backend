package com.example.musinsabackend.repository.user;

import com.example.musinsabackend.model.Like;
import com.example.musinsabackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUser_UserIdAndProduct_Id(Long userId, Long productId);

    boolean existsByUser_UserIdAndProduct_Id(Long userId, Long productId);

    Long countByProductId(Long productId);

    void deleteByUser_UserIdAndProduct_Id(Long userUserId, Long productId);

    // ✅ 좋아요 수 기준 인기 상품 10개 조회
    @Query("SELECT p FROM Product p ORDER BY p.likeCount DESC")
    List<Product> findTop10ByOrderByLikeCountDesc();

    // ✅ 상품 삭제 시 해당 상품의 좋아요 데이터 삭제
    void deleteByProductId(Long productId);
}