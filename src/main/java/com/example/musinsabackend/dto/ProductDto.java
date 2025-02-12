package com.example.musinsabackend.dto;

import com.example.musinsabackend.model.Product;
import com.example.musinsabackend.model.ProductCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDto {
    private Long id;
    private String name;
    private Double price;
    private String description;
    private List<String> images; // ✅ 여러 장의 이미지 저장
    private List<String> sizes;  // ✅ JSON 변환 없이 유지
    private List<String> colors; // ✅ JSON 변환 없이 유지
    private String brandName;    // 브랜드명
    private String brandLogoUrl; // ✅ 브랜드 로고
    private ProductCategory category; // ✅ 상품 카테고리
    private int likeCount;       // ✅ 좋아요 수
    private boolean likedByCurrentUser; // ✅ 현재 로그인한 사용자의 좋아요 여부

    // ✅ 생성자
    public ProductDto(Long id, String name, Double price, String description,
                      List<String> images, List<String> sizes, List<String> colors,
                      String brandName, String brandLogoUrl, ProductCategory category,
                      int likeCount, boolean likedByCurrentUser) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.images = images != null ? new ArrayList<>(images) : new ArrayList<>();
        this.sizes = sizes != null ? new ArrayList<>(sizes) : new ArrayList<>();
        this.colors = colors != null ? new ArrayList<>(colors) : new ArrayList<>();
        this.brandName = brandName;
        this.brandLogoUrl = brandLogoUrl;
        this.category = category;
        this.likeCount = likeCount;
        this.likedByCurrentUser = likedByCurrentUser;
    }

    // ✅ 엔티티 → DTO 변환 메서드
    public static ProductDto fromEntity(Product product) {
        List<String> imageUrls = product.getImages().stream()
                .map(image -> "http://localhost:8080" + image) // ✅ 절대 경로로 변환
                .collect(Collectors.toList());

        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                imageUrls,
                product.getSizes(),
                product.getColors(),
                product.getBrand() != null ? product.getBrand().getName() : "",
                product.getBrand() != null ? "http://localhost:8080/uploads/brand-logos/" + product.getBrand().getLogoUrl() : "",
                product.getCategory(),
                product.getLikeCount(),
                product.isLikedByCurrentUser()
        );
    }

    // ✅ Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images != null ? images : List.of(); }

    public List<String> getSizes() { return sizes; }
    public void setSizes(List<String> sizes) { this.sizes = sizes != null ? sizes : List.of(); }

    public List<String> getColors() { return colors; }
    public void setColors(List<String> colors) { this.colors = colors != null ? colors : List.of(); }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getBrandLogoUrl() { return brandLogoUrl; }
    public void setBrandLogoUrl(String brandLogoUrl) { this.brandLogoUrl = brandLogoUrl; }

    public ProductCategory getCategory() { return category; }
    public void setCategory(ProductCategory category) { this.category = category; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public boolean isLikedByCurrentUser() { return likedByCurrentUser; }
    public void setLikedByCurrentUser(boolean likedByCurrentUser) { this.likedByCurrentUser = likedByCurrentUser; }
}
