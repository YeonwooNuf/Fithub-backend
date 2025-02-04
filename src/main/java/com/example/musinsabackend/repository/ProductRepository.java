package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.Product;
import com.example.musinsabackend.model.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ✅ 상품 이름으로 검색 (대소문자 무시) + 페이지네이션 적용
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // ✅ 특정 브랜드의 상품 검색 (Brand 엔티티를 직접 참조)
    Page<Product> findByBrand(Brand brand, Pageable pageable);
}
