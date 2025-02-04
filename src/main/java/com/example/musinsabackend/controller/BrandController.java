package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.BrandDto;
import com.example.musinsabackend.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    // ✅ 브랜드 목록 조회
    @GetMapping
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    // ✅ 브랜드명으로 검색 (대소문자 무시)
    @GetMapping("/search")
    public ResponseEntity<List<BrandDto>> searchBrandByName(@RequestParam String query) {
        return ResponseEntity.ok(brandService.searchBrandByName(query));
    }

    // ✅ 브랜드 추가 (파일 업로드 포함)
    @PostMapping
    public ResponseEntity<BrandDto> createBrand(
            @RequestParam("name") String name,
            @RequestParam("subName") String subName,
            @RequestParam("logo") MultipartFile logoFile) {
        return ResponseEntity.ok(brandService.createBrand(name, subName, logoFile));
    }

    // ✅ 브랜드 수정 (파일 업로드 포함)
    @PutMapping("/{id}")
    public ResponseEntity<BrandDto> updateBrand(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("subName") String subName,
            @RequestParam(value = "logo", required = false) MultipartFile logoFile) {
        return ResponseEntity.ok(brandService.updateBrand(id, name, subName, logoFile));
    }

    // ✅ 브랜드 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok("브랜드 삭제 완료");
    }
}
