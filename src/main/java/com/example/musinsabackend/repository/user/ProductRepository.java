package com.example.musinsabackend.repository.user;

import com.example.musinsabackend.model.Product;
import com.example.musinsabackend.model.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ✅ 상품 이름으로 검색 (대소문자 무시) + 페이지네이션 적용
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // ✅ 브랜드 ID로 상품 검색
    Page<Product> findByBrandId(Long brandId, Pageable pageable);

    // ✅ 카테고리로 상품 검색
    Page<Product> findByCategory(ProductCategory category, Pageable pageable);

    // 상품 상세 조회 (이미지 포함)
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithImages(@Param("id") Long id);

    // ✅ 좋아요 수 기준으로 상위 10개 상품 조회
    List<Product> findTop10ByOrderByLikeCountDesc();
}
