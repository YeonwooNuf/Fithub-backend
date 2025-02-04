package com.example.musinsabackend.repository;

import com.example.musinsabackend.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> findByNameOrSubNameIgnoreCase(String name, String subName);
}
