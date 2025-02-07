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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/clothes-images/";

    public ProductService(ProductRepository productRepository, BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
    }

    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductDto::fromEntity);
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
        return ProductDto.fromEntity(product);
    }

    public Page<ProductDto> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(ProductDto::fromEntity);
    }

    public Page<ProductDto> getProductsByBrand(Long brandId, Pageable pageable) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("해당 브랜드가 존재하지 않습니다."));
        return productRepository.findByBrand(brand, pageable).map(ProductDto::fromEntity);
    }

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
        product.setSizes(Optional.ofNullable(productDto.getSizes()).orElse(new ArrayList<>()));
        product.setColors(Optional.ofNullable(productDto.getColors()).orElse(new ArrayList<>()));
        product.setCategory(productDto.getCategory());
        product.setBrand(brand);

        productRepository.save(product);
        return ProductDto.fromEntity(product);
    }

    public ProductDto updateProduct(Long id, ProductDto productDto, List<MultipartFile> imageFiles) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        List<String> imageUrls = (imageFiles != null && !imageFiles.isEmpty()) ? saveFiles(imageFiles) : existingProduct.getImages();

        if (imageFiles != null && !imageFiles.isEmpty()) {
            deleteFiles(existingProduct.getImages());
        }

        existingProduct.setName(productDto.getName());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setImages(imageUrls);
        existingProduct.setSizes(Optional.ofNullable(productDto.getSizes()).orElse(new ArrayList<>()));
        existingProduct.setColors(Optional.ofNullable(productDto.getColors()).orElse(new ArrayList<>()));
        existingProduct.setCategory(productDto.getCategory());

        productRepository.save(existingProduct);
        return ProductDto.fromEntity(existingProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
        deleteFiles(product.getImages());
        productRepository.delete(product);
    }

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

                    String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFilename;
                    Path filePath = uploadPath.resolve(uniqueFileName);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    return "/uploads/clothes-images/" + uniqueFileName;
                } catch (Exception e) {
                    throw new RuntimeException("파일 저장 중 오류 발생: " + e.getMessage());
                }
            }).collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("디렉토리 생성 실패: " + e.getMessage());
        }
    }

    private void deleteFiles(List<String> imageUrls) {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            imageUrls.forEach(imageUrl -> {
                try {
                    Path filePath = Paths.get(System.getProperty("user.dir") + imageUrl);
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    System.err.println("파일 삭제 실패: " + e.getMessage());
                }
            });
        }
    }

    private void validateImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            throw new IllegalArgumentException("상품 이미지는 필수입니다.");
        }
    }

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
