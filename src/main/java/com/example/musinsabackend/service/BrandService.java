package com.example.musinsabackend.service;

import com.example.musinsabackend.model.Brand;
import com.example.musinsabackend.repository.BrandRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    // ✅ 모든 브랜드 조회
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    // ✅ 특정 브랜드 조회
    public Brand getBrandById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 존재하지 않습니다."));
    }

    // ✅ 브랜드 추가
    public Brand addBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    // ✅ 브랜드 수정
    public Brand updateBrand(Long id, Brand updatedBrand) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 존재하지 않습니다."));

        brand.setName(updatedBrand.getName());
        brand.setLogoUrl(updatedBrand.getLogoUrl());

        return brandRepository.save(brand);
    }

    // ✅ 브랜드 삭제
    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 브랜드가 존재하지 않습니다.");
        }
        brandRepository.deleteById(id);
    }
}
