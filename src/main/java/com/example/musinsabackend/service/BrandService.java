package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.BrandDto;
import com.example.musinsabackend.model.Brand;
import com.example.musinsabackend.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private static final String UPLOAD_DIR = "uploads/brand-logos/";

    // ✅ 모든 브랜드 조회
    public List<BrandDto> getAllBrands() {
        return brandRepository.findAll()
                .stream()
                .map(BrandDto::fromEntity)
                .collect(Collectors.toList());
    }

    // ✅ 브랜드명(영어/한글)으로 검색
    public List<BrandDto> searchBrandByName(String query) {
        return brandRepository.findByNameOrSubNameIgnoreCase(query, query)
                .stream()
                .map(BrandDto::fromEntity)
                .collect(Collectors.toList());
    }

    // ✅ 브랜드 추가 (파일 업로드 포함)
    public BrandDto createBrand(String name, String subName, MultipartFile logoFile) {
        String logoUrl = saveFile(logoFile);

        Brand brand = new Brand();
        brand.setName(name);
        brand.setSubName(subName); // 🔥 한글 브랜드명 추가
        brand.setLogoUrl(logoUrl);

        brandRepository.save(brand);
        return BrandDto.fromEntity(brand);
    }

    // ✅ 브랜드 수정
    public BrandDto updateBrand(Long id, String name, String subName, MultipartFile logoFile) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 존재하지 않습니다."));

        brand.setName(name);
        brand.setSubName(subName); // 🔥 한글 브랜드명 수정 가능
        if (logoFile != null && !logoFile.isEmpty()) {
            brand.setLogoUrl(saveFile(logoFile));
        }

        brandRepository.save(brand);
        return BrandDto.fromEntity(brand);
    }

    // ✅ 브랜드 삭제
    public void deleteBrand(Long id) {
        brandRepository.deleteById(id);
    }

    // ✅ 파일 저장 메소드
    private String saveFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            return "/uploads/brand-logos/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 중 오류 발생: " + e.getMessage());
        }
    }
}
