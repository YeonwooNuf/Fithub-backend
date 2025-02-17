package com.example.musinsabackend.controller.admin;

import com.example.musinsabackend.dto.ProductDto;
import com.example.musinsabackend.model.ProductCategory;
import com.example.musinsabackend.service.admin.AdminProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminProductController {

    private final AdminProductService productService;

    public AdminProductController(AdminProductService productService) {
        this.productService = productService;
    }

    // ✅ 1. 전체 상품 조회 (페이지네이션 & DTO 적용)
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

    // ✅ 2. 특정 상품 조회 (DTO 변환)
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // ✅ 3. 상품 검색 (이름 포함된 상품 조회, 페이지네이션 포함)
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

    // ✅ 4. 상품 추가 (이미지 필수 입력)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDto> addProduct(
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("brandId") Long brandId,
            @RequestParam("category") String categoryStr,
            @RequestParam(value = "sizes", required = false) List<String> sizes,
            @RequestParam(value = "colors", required = false) List<String> colors,
            @RequestPart(value = "images", required = true) List<MultipartFile> images  // ✅ 이미지 필수
    ) {
        validateImages(images);

        ProductCategory category = convertToCategory(categoryStr);

        ProductDto productDto = new ProductDto(
                null, name, price, description, null,
                sizes != null ? sizes : List.of(""),   // ✅ NULL 방지
                colors != null ? colors : List.of(""), // ✅ NULL 방지
                null, null,null, category, 0, false          // ✅ 좋아요 상태 기본값 false
        );

        return ResponseEntity.ok(productService.addProduct(productDto, brandId, images));
    }

    // ✅ 5. 상품 수정 (이미지 필수 입력)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("brandId") Long brandId,
            @RequestParam("category") String categoryStr,
            @RequestParam(value = "sizes", required = false) List<String> sizes,
            @RequestParam(value = "colors", required = false) List<String> colors,
            @RequestPart(value = "images", required = true) List<MultipartFile> images // ✅ 이미지 필수
    ) {
        validateImages(images);

        ProductCategory category = convertToCategory(categoryStr);

        ProductDto productDto = new ProductDto(
                id, name, price, description, null,
                sizes != null ? sizes : List.of(""),   // ✅ NULL 방지
                colors != null ? colors : List.of(""), // ✅ NULL 방지
                null, null, null, category, 0, false          // ✅ 좋아요 상태 기본값 false
        );

        return ResponseEntity.ok(productService.updateProduct(id, productDto, images));
    }

    // ✅ 6. 상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message", "상품이 삭제되었습니다."));
    }

    // ✅ 이미지 유효성 검사 (비어있으면 예외 발생)
    private void validateImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            throw new IllegalArgumentException("상품 이미지는 필수입니다.");
        }
    }

    // ✅ 카테고리 변환 유틸리티
    private ProductCategory convertToCategory(String categoryStr) {
        try {
            return ProductCategory.valueOf(categoryStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 카테고리 값입니다: " + categoryStr);
        }
    }
}
