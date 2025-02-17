package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.BrandDto;
import com.example.musinsabackend.model.Brand;
import com.example.musinsabackend.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    private static final String UPLOAD_DIR = "/app/uploads/brand-logos/";

    // ✅ 모든 브랜드 조회
    public List<BrandDto> getAllBrands() {
        return brandRepository.findAll()
                .stream()
                .map(this::convertToBrandDto)
                .collect(Collectors.toList());
    }

    // ✅ 브랜드명(영어/한글)으로 검색
    public List<BrandDto> searchBrandByName(String query) {
        return brandRepository.findByNameOrSubNameIgnoreCase(query, query)
                .stream()
                .map(this::convertToBrandDto)
                .collect(Collectors.toList());
    }

    // ✅ 브랜드 추가 (파일 업로드 포함)
    public BrandDto createBrand(String name, String subName, MultipartFile logoFile) {
        String fileName = saveFile(logoFile); // ✅ 원본 파일명 저장

        Brand brand = new Brand();
        brand.setName(name);
        brand.setSubName(subName);
        brand.setLogoUrl(fileName); // ✅ DB에는 파일명만 저장

        brandRepository.save(brand);
        return convertToBrandDto(brand);
    }

    // ✅ 브랜드 수정 (파일 업로드 포함)
    public BrandDto updateBrand(Long id, String name, String subName, MultipartFile logoFile) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 존재하지 않습니다."));

        brand.setName(name);
        brand.setSubName(subName);
        if (logoFile != null && !logoFile.isEmpty()) {
            deleteFile(brand.getLogoUrl()); // ✅ 기존 파일 삭제
            brand.setLogoUrl(saveFile(logoFile)); // ✅ 새 파일 저장 후 파일명만 저장
        }

        brandRepository.save(brand);
        return convertToBrandDto(brand);
    }

    // ✅ 브랜드 삭제 (로고 파일도 삭제)
    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 존재하지 않습니다."));

        deleteFile(brand.getLogoUrl());
        brandRepository.deleteById(id);
    }

    // ✅ 파일 저장 (중복 방지)
    private String saveFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(originalFileName);

            // ✅ 같은 이름의 파일이 존재하면 파일명 뒤에 (1), (2) 붙이기
            int count = 1;
            while (Files.exists(filePath)) {
                String fileNameWithoutExt = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
                String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
                String newFileName = fileNameWithoutExt + "(" + count + ")" + extension;
                filePath = uploadPath.resolve(newFileName);
                count++;
            }

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filePath.getFileName().toString(); // ✅ DB에는 파일명만 저장
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생: " + e.getMessage());
        }
    }

    // ✅ 파일 삭제
    private void deleteFile(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            try {
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("❌ 파일 삭제 실패: " + e.getMessage());
            }
        }
    }

    // ✅ API 응답을 위한 변환 (baseUrl 추가)
    private BrandDto convertToBrandDto(Brand brand) {
        return new BrandDto(
                brand.getId(),
                brand.getName(),
                brand.getSubName(),
                brand.getLogoUrl() != null ? baseUrl + "/uploads/brand-logos/" + brand.getLogoUrl() : null
        );
    }
}
