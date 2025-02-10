package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.BrandDto;
import com.example.musinsabackend.model.Brand;
import com.example.musinsabackend.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    // ✅ Docker에서도 동작하도록 절대 경로 설정
    private static final String UPLOAD_DIR = "/app/uploads/brand-logos/";

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
        brand.setSubName(subName);
        brand.setLogoUrl(logoUrl);

        brandRepository.save(brand);
        return BrandDto.fromEntity(brand);
    }

    // ✅ 브랜드 수정
    public BrandDto updateBrand(Long id, String name, String subName, MultipartFile logoFile) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 존재하지 않습니다."));

        brand.setName(name);
        brand.setSubName(subName);
        if (logoFile != null && !logoFile.isEmpty()) {
            brand.setLogoUrl(saveFile(logoFile));
        }

        brandRepository.save(brand);
        return BrandDto.fromEntity(brand);
    }

    // ✅ 브랜드 삭제 (로고 파일도 삭제)
    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 존재하지 않습니다."));

        deleteFile(brand.getLogoUrl());
        brandRepository.deleteById(id);
    }

    // ✅ 파일 저장 메소드
    private String saveFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // ✅ 파일 이름 중복 방지 (UUID 추가)
            String originalFileName = file.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
            Path filePath = uploadPath.resolve(uniqueFileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/brand-logos/" + uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생: " + e.getMessage());
        }
    }

    // ✅ 파일 삭제 메소드
    private void deleteFile(String logoUrl) {
        try {
            Path filePath = Paths.get(System.getProperty("user.dir") + logoUrl);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            System.out.println("❌ 파일 삭제 중 오류 발생: " + e.getMessage());
        }
    }
}
