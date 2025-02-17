package com.example.musinsabackend.service.user;

import com.example.musinsabackend.dto.ProductDto;
import com.example.musinsabackend.model.Product;
import com.example.musinsabackend.model.ProductCategory;
import com.example.musinsabackend.model.User;
import com.example.musinsabackend.repository.user.LikeRepository;
import com.example.musinsabackend.repository.user.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final LikeRepository likeRepository;

    @Value("${app.base-url}") // ✅ BASE_URL을 application.properties에서 설정
    private String baseUrl;

    public ProductService(ProductRepository productRepository, LikeRepository likeRepository) {
        this.productRepository = productRepository;
        this.likeRepository = likeRepository;
    }

    // 현재 로그인한 사용자 정보 가져오기
    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            return user.getUserId();
        }
        return null;
    }

    // 전체 상품 목록 조회 (페이징 지원)
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        Long userId = getCurrentUserId();
        return productRepository.findAll(pageable)
                .map(product -> mapToProductDtoWithLikeStatus(product, userId));
    }

    // 상품 상세 정보 조회
    public ProductDto getProductById(Long id) {
        Long userId = getCurrentUserId();
        Product product = productRepository.findByIdWithImages(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        return mapToProductDtoWithLikeStatus(product, userId);
    }

    // 키워드로 상품 검색
    public Page<ProductDto> searchProducts(String keyword, Pageable pageable) {
        Long userId = getCurrentUserId();
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable)
                .map(product -> mapToProductDtoWithLikeStatus(product, userId));
    }

    // 브랜드별 상품 조회
    public Page<ProductDto> getProductsByBrand(Long brandId, Pageable pageable) {
        Long userId = getCurrentUserId();
        return productRepository.findByBrandId(brandId, pageable)
                .map(product -> mapToProductDtoWithLikeStatus(product, userId));
    }

    // 카테고리별 상품 조회
    public Page<ProductDto> getProductsByCategory(String category, Pageable pageable) {
        Long userId = getCurrentUserId();
        ProductCategory productCategory = ProductCategory.valueOf(category.toUpperCase());

        return productRepository.findByCategory(productCategory, pageable)
                .map(product -> mapToProductDtoWithLikeStatus(product, userId));
    }

    // 인기 상품 조회 (좋아요 기반)
    public List<ProductDto> getPopularProducts() {
        Long userId = getCurrentUserId();
        return productRepository.findTop10ByOrderByLikeCountDesc()
                .stream()
                .map(product -> mapToProductDtoWithLikeStatus(product, userId))
                .collect(Collectors.toList());
    }

    // ✅ 좋아요 상태 설정 후 DTO 변환 (브랜드 로고 추가)
    private ProductDto mapToProductDtoWithLikeStatus(Product product, Long userId) {
        boolean liked = false;

        // 로그인한 사용자의 좋아요 상태 확인
        if (userId != null) {
            liked = likeRepository.existsByUser_UserIdAndProduct_Id(userId, product.getId());
        }

        // ✅ 브랜드 정보 포함
        String brandName = (product.getBrand() != null) ? product.getBrand().getName() : "";
        String brandSubName = (product.getBrand() != null) ? product.getBrand().getSubName() : "";
        String brandLogoUrl = (product.getBrand() != null && product.getBrand().getLogoUrl() != null)
                ? baseUrl + "/uploads/brand-logos/" + product.getBrand().getLogoUrl()
                : null;

        // ✅ 상품 이미지 URL 절대경로 변환
        List<String> imageUrls = product.getImages().stream()
                .map(image -> baseUrl + image)
                .collect(Collectors.toList());

        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                imageUrls,
                product.getSizes(),
                product.getColors(),
                brandName,
                brandSubName,
                brandLogoUrl,
                product.getCategory(),
                product.getLikeCount(),
                liked
        );
    }
}
