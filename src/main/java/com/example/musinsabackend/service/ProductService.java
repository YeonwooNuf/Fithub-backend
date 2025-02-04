package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.ProductDto;
import com.example.musinsabackend.model.Brand;
import com.example.musinsabackend.model.Product;
import com.example.musinsabackend.model.ProductCategory;
import com.example.musinsabackend.repository.BrandRepository;
import com.example.musinsabackend.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    public ProductService(ProductRepository productRepository, BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
    }

    // ✅ 전체 상품 조회 (페이지네이션 적용 & DTO 변환)
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductDto::fromEntity);
    }

    // ✅ 특정 상품 조회 (DTO 변환)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
        return ProductDto.fromEntity(product);
    }

    // ✅ 상품 검색 (이름 기준, 페이지네이션 적용 & DTO 변환)
    public Page<ProductDto> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(ProductDto::fromEntity);
    }

    // ✅ 특정 브랜드의 상품 조회 (DTO 변환)
    public Page<ProductDto> getProductsByBrand(Long brandId, Pageable pageable) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 존재하지 않습니다."));
        return productRepository.findByBrand(brand, pageable).map(ProductDto::fromEntity);
    }

    // ✅ 상품 추가 (카테고리 필드 포함)
    public ProductDto addProduct(ProductDto productDto, Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 존재하지 않습니다."));

        Product product = new Product();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setImageUrl(productDto.getImageUrl());
        product.setSizes(productDto.getSizes());
        product.setColors(productDto.getColors());
        product.setCategory(productDto.getCategory()); // ✅ 카테고리 추가
        product.setBrand(brand);

        productRepository.save(product);
        return ProductDto.fromEntity(product);
    }

    // ✅ 상품 수정 (카테고리 포함)
    public ProductDto updateProduct(Long id, ProductDto productDto, Long brandId) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 존재하지 않습니다."));

        existingProduct.setName(productDto.getName());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setImageUrl(productDto.getImageUrl());
        existingProduct.setSizes(productDto.getSizes());
        existingProduct.setColors(productDto.getColors());
        existingProduct.setCategory(productDto.getCategory()); // ✅ 카테고리 추가
        existingProduct.setBrand(brand);

        productRepository.save(existingProduct);
        return ProductDto.fromEntity(existingProduct);
    }

    // ✅ 상품 삭제
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
        productRepository.delete(product);
    }
}
