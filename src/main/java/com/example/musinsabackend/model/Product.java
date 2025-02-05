package com.example.musinsabackend.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Product {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false, length = 1000)
    private String description;

    // ✅ 여러 장의 이미지 저장
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url", nullable = false)
    private List<String> images;

    // ✅ JSON 문자열로 저장 (사이즈 리스트)
    @Column(columnDefinition = "TEXT", nullable = false)
    private String sizes;

    // ✅ JSON 문자열로 저장 (색상 리스트)
    @Column(columnDefinition = "TEXT", nullable = false)
    private String colors;

    // ✅ 브랜드 연결
    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    // ✅ 상품 카테고리 추가 (ENUM)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    // ✅ 기본 생성자
    public Product() {}

    // ✅ 생성자
    public Product(String name, Double price, String description, List<String> images,
                   List<String> sizes, List<String> colors, Brand brand, ProductCategory category) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.images = images;
        setSizes(sizes); // JSON 변환
        setColors(colors); // JSON 변환
        this.brand = brand;
        this.category = category;
    }

    // ✅ JSON 변환을 위한 Getter
    public List<String> getSizes() {
        try {
            return objectMapper.readValue(sizes, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of(); // 변환 실패 시 빈 리스트 반환
        }
    }

    public List<String> getColors() {
        try {
            return objectMapper.readValue(colors, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    // ✅ JSON 변환을 위한 Setter
    public void setSizes(List<String> sizes) {
        try {
            this.sizes = objectMapper.writeValueAsString(sizes);
        } catch (Exception e) {
            this.sizes = "[]"; // 변환 실패 시 빈 JSON 배열 저장
        }
    }

    public void setColors(List<String> colors) {
        try {
            this.colors = objectMapper.writeValueAsString(colors);
        } catch (Exception e) {
            this.colors = "[]";
        }
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
    public void setImages(List<String> images) { this.images = images; }

    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }

    public ProductCategory getCategory() { return category; }
    public void setCategory(ProductCategory category) { this.category = category; }
}
