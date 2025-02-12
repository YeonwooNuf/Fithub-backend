package com.example.musinsabackend.controller.user;

import com.example.musinsabackend.dto.ProductDto;
import com.example.musinsabackend.service.user.ProductService;
import com.example.musinsabackend.jwt.JwtTokenProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    private final ProductService productService;
    private final JwtTokenProvider jwtTokenProvider;

    public ProductController(ProductService productService, JwtTokenProvider jwtTokenProvider) {
        this.productService = productService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    private Long extractUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtTokenProvider.getUserIdFromToken(token);
        }
        return null;
    }

    // ✅ 1. 전체 상품 목록 조회 (이미지 및 좋아요 여부 포함)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductDto> productPage = productService.getAllProducts(PageRequest.of(page, size));
        return ResponseEntity.ok(Map.of(
                "products", productPage.getContent(),
                "currentPage", productPage.getNumber(),
                "totalPages", productPage.getTotalPages()
        ));
    }

    // ✅ 2. 상품 상세 조회 (이미지 및 좋아요 여부 포함)
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto productDto = productService.getProductById(id);
        return ResponseEntity.ok(productDto);
    }

    // ✅ 3. 키워드 검색 (이미지 및 좋아요 여부 포함)
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
}
