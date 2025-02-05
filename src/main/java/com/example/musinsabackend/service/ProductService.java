package com.example.musinsabackend.service;

import com.example.musinsabackend.dto.ProductDto;
import com.example.musinsabackend.model.Brand;
import com.example.musinsabackend.model.Product;
import com.example.musinsabackend.repository.BrandRepository;
import com.example.musinsabackend.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private static final String UPLOAD_DIR = "uploads/clothes-images/";

    public ProductService(ProductRepository productRepository, BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
    }

    // ✅ 전체 상품 조회 (DTO 변환)
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductDto::fromEntity);
    }

    // ✅ 특정 상품 조회
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
        return ProductDto.fromEntity(product);
    }

    // ✅ 상품 검색
    public Page<ProductDto> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(ProductDto::fromEntity);
    }

    // ✅ 특정 브랜드의 상품 조회
    public Page<ProductDto> getProductsByBrand(Long brandId, Pageable pageable) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 존재하지 않습니다."));
        return productRepository.findByBrand(brand, pageable).map(ProductDto::fromEntity);
    }

    // ✅ 상품 추가 (여러 장의 이미지 저장, 이미지 없으면 예외 발생)
    public ProductDto addProduct(ProductDto productDto, Long brandId, List<MultipartFile> imageFiles) {
        validateImages(imageFiles);

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 존재하지 않습니다."));

        List<String> imageUrls = saveFiles(imageFiles);

        Product product = new Product();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setImages(imageUrls);

        // ✅ colors와 sizes가 NULL이면 빈 리스트를 저장
        product.setSizes(productDto.getSizes() != null ? productDto.getSizes() : new ArrayList<>());
        product.setColors(productDto.getColors() != null ? productDto.getColors() : new ArrayList<>());

        product.setCategory(productDto.getCategory());
        product.setBrand(brand);

        productRepository.save(product);
        return ProductDto.fromEntity(product);
    }

    // ✅ 상품 수정 (기존 이미지 삭제 후 저장)
    public ProductDto updateProduct(Long id, ProductDto productDto, List<MultipartFile> imageFiles) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        List<String> imageUrls;
        if (imageFiles != null && !imageFiles.isEmpty()) {
            // ✅ 기존 이미지 삭제 후 새 이미지 저장
            deleteFiles(existingProduct.getImages());
            imageUrls = saveFiles(imageFiles);
        } else {
            imageUrls = existingProduct.getImages(); // ✅ 기존 이미지 유지
        }

        existingProduct.setName(productDto.getName());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setImages(imageUrls);

        // ✅ colors와 sizes가 NULL이면 빈 리스트를 저장
        existingProduct.setSizes(productDto.getSizes() != null ? productDto.getSizes() : new ArrayList<>());
        existingProduct.setColors(productDto.getColors() != null ? productDto.getColors() : new ArrayList<>());

        existingProduct.setCategory(productDto.getCategory());

        productRepository.save(existingProduct);
        return ProductDto.fromEntity(existingProduct);
    }

    // ✅ 상품 삭제 (연결된 이미지 삭제)
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
        deleteFiles(product.getImages());
        productRepository.delete(product);
    }

    // ✅ 파일 저장 메소드 (이미지 확장자 검사 포함)
    private List<String> saveFiles(List<MultipartFile> files) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            return files.stream().map(file -> {
                try {
                    String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
                    validateFileExtension(originalFilename);

                    String fileName = originalFilename;
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    return "/uploads/clothes-images/" + fileName;
                } catch (Exception e) {
                    throw new RuntimeException("파일 저장 중 오류 발생: " + e.getMessage());
                }
            }).collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("디렉토리 생성 실패: " + e.getMessage());
        }
    }

    // ✅ 파일 삭제 메소드 (상품 수정/삭제 시 호출)
    private void deleteFiles(List<String> imageUrls) {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            imageUrls.forEach(imageUrl -> {
                try {
                    String fileName = Paths.get(imageUrl).getFileName().toString();
                    Path filePath = Paths.get(UPLOAD_DIR, fileName);
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    System.err.println("파일 삭제 실패: " + e.getMessage());
                }
            });
        }
    }

    // ✅ 이미지 유효성 검사 (비어있으면 예외 발생)
    private void validateImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            throw new IllegalArgumentException("상품 이미지는 필수입니다.");
        }
    }

    // ✅ 파일 확장자 검사
    private void validateFileExtension(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();
        if (!(lowerCaseFileName.endsWith(".jpg") ||
                lowerCaseFileName.endsWith(".jpeg") ||
                lowerCaseFileName.endsWith(".png") ||
                lowerCaseFileName.endsWith(".webp"))) {
            throw new IllegalArgumentException("지원되지 않는 파일 형식입니다: " + fileName);
        }
    }
}
