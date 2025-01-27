package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 상품 이름으로 검색
    List<Product> findByNameContaining(String name);

    // 특정 색상에 따라 검색
    List<Product> findByColor(String color);

    // 특정 사이즈에 따라 검색
    List<Product> findBySize(String size);
}
