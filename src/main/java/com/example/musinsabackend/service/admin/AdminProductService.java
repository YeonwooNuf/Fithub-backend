package com.example.musinsabackend.service.admin;

import com.example.musinsabackend.dto.ProductDto;
import com.example.musinsabackend.model.Brand;
import com.example.musinsabackend.model.Product;
import com.example.musinsabackend.repository.BrandRepository;
import com.example.musinsabackend.repository.admin.AdminProductRepository;
import com.example.musinsabackend.repository.user.LikeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminProductService {

    private final AdminProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final LikeRepository likeRepository; // ✅ 좋아요 데이터 관리 (Optional)

    private static final String UPLOAD_DIR = "/app/uploads/cloth-images/";

    public AdminProductService(AdminProductRepository productRepository, BrandRepository brandRepository, LikeRepository likeRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.likeRepository = likeRepository;
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
        product.setSizes(productDto.getSizes());
        product.setColors(productDto.getColors());
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
        existingProduct.setSizes(productDto.getSizes());
        existingProduct.setColors(productDto.getColors());
        existingProduct.setCategory(productDto.getCategory());

        productRepository.save(existingProduct);
        return ProductDto.fromEntity(existingProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        // ✅ 좋아요 데이터 삭제 (Optional)
        likeRepository.deleteByProductId(product.getId());

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

                    return "/uploads/cloth-images/" + uniqueFileName;
                } catch (IOException e) {
                    throw new IllegalStateException("파일 저장 중 오류 발생: " + e.getMessage());
                }
            }).collect(Collectors.toList());

        } catch (IOException e) {
            throw new IllegalStateException("디렉토리 생성 실패: " + e.getMessage());
        }
    }

    private void deleteFiles(List<String> imageUrls) {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            imageUrls.forEach(imageUrl -> {
                try {
                    Path filePath = Paths.get(UPLOAD_DIR + imageUrl.substring(imageUrl.lastIndexOf("/") + 1));
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
