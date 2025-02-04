package com.example.musinsabackend.dto;

import com.example.musinsabackend.model.Brand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  // 기본 생성자 추가
@AllArgsConstructor // 모든 필드를 포함한 생성자 추가
public class BrandDto {

    private Long id;
    private String name;     // 영어 브랜드명
    private String subName;  // 한글 브랜드명
    private String logoUrl;  // 로고 이미지 URL

    // ✅ Brand 엔티티 → BrandDto 변환
    public static BrandDto fromEntity(Brand brand) {
        return new BrandDto(
                brand.getId(),
                brand.getName(),
                brand.getSubName(),
                brand.getLogoUrl()
        );
    }
}
