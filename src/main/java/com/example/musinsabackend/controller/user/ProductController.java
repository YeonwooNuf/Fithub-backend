package com.example.musinsabackend.controller.user;

import com.example.musinsabackend.dto.ProductDto;
import com.example.musinsabackend.repository.user.LikeRepository;
import com.example.musinsabackend.service.user.ProductService;
import com.example.musinsabackend.jwt.JwtTokenProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    private final ProductService productService;
    private final JwtTokenProvider jwtTokenProvider;
    private final LikeRepository likeRepository;

    public ProductController(ProductService productService, JwtTokenProvider jwtTokenProvider, LikeRepository likeRepository) {
        this.productService = productService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.likeRepository = likeRepository;
    }

    private Long extractUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtTokenProvider.getUserIdFromToken(token);
        }
        return null;
    }

    // ✅ 1. 전체 상품 목록 조회 (이미지, 좋아요 여부, 브랜드 로고 포함)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // ✅ currentUserId를 effectively final 변수로 변경
        final Long currentUserId;

        // ✅ JWT 토큰이 있는 경우에만 검증
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            currentUserId = jwtTokenProvider.validateToken(token) ? jwtTokenProvider.getUserIdFromToken(token) : null;
        } else {
            currentUserId = null;
        }

        // ✅ 페이지네이션된 상품 목록 가져오기
        Page<ProductDto> productPage = productService.getAllProducts(PageRequest.of(page, size));

        // ✅ 각 상품에 likedByCurrentUser 추가
        List<Map<String, Object>> updatedProducts = productPage.getContent().stream()
                .map(productDto -> {
                    Map<String, Object> productMap = new HashMap<>();
                    productMap.put("id", productDto.getId());
                    productMap.put("name", productDto.getName());
                    productMap.put("price", productDto.getPrice());
                    productMap.put("likeCount", productDto.getLikeCount());
                    productMap.put("images", productDto.getImages());
                    productMap.put("brandName", productDto.getBrandName());
                    productMap.put("brandLogoUrl", productDto.getBrandLogoUrl());

                    // ✅ 로그인한 경우에만 좋아요 여부 확인
                    boolean likedByCurrentUser = currentUserId != null &&
                            likeRepository.existsByUser_UserIdAndProduct_Id(currentUserId, productDto.getId());

                    productMap.put("likedByCurrentUser", likedByCurrentUser);
                    return productMap;
                })
                .collect(Collectors.toList());

        // ✅ 최종 반환
        return ResponseEntity.ok(Map.of(
                "products", updatedProducts,
                "currentPage", productPage.getNumber(),
                "totalPages", productPage.getTotalPages()
        ));
    }

    // ✅ 2. 상품 상세 조회 (이미지, 좋아요 여부, 브랜드 로고 포함)
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto productDto = productService.getProductById(id);
        return ResponseEntity.ok(productDto);
    }

    // ✅ 3. 키워드 검색 (이미지, 좋아요 여부, 브랜드 로고 포함)
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductDto> productPage = productService.searchProducts(keyword, PageRequest.of(page, size));
        return ResponseEntity.ok(Map.of(
                "products", productPage.getContent(),
                "currentPage", productPage.getNumber(),
                "totalPages", productPage.getTotalPages()
        ));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<ProductDto>> getLatestProducts() {
        List<ProductDto> latestProducts = productService.getLatestProducts(5);
        return ResponseEntity.ok(latestProducts);
    }

}
