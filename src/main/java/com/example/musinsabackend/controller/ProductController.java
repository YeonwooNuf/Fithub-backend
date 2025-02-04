package com.example.musinsabackend.controller;

import com.example.musinsabackend.dto.ProductDto;
import com.example.musinsabackend.model.ProductCategory;
import com.example.musinsabackend.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
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

    // ✅ 4. 상품 추가 (DTO 변환 & 카테고리 추가)
    @PostMapping
    public ResponseEntity<ProductDto> addProduct(@RequestBody Map<String, Object> requestData) {
        Long brandId = Long.parseLong(requestData.get("brandId").toString());

        ProductDto productDto = new ProductDto(
                null,
                (String) requestData.get("name"),
                Double.valueOf(requestData.get("price").toString()),
                (String) requestData.get("description"),
                (String) requestData.get("imageUrl"),
                (List<String>) requestData.get("sizes"),
                (List<String>) requestData.get("colors"),
                (String) requestData.get("category"),
                null,
                null
        );

        return ResponseEntity.ok(productService.addProduct(productDto, brandId));
    }

    // ✅ 5. 상품 수정 (DTO 변환 & 카테고리 추가)
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @RequestBody Map<String, Object> requestData
    ) {
        Long brandId = Long.parseLong(requestData.get("brandId").toString());

        ProductDto productDto = new ProductDto(
                id,
                (String) requestData.get("name"),
                Double.valueOf(requestData.get("price").toString()),
                (String) requestData.get("description"),
                (String) requestData.get("imageUrl"),
                (List<String>) requestData.get("sizes"),
                (List<String>) requestData.get("colors"),
                (String) requestData.get("category"), // ✅ 카테고리 추가
                null,
                null
        );

        return ResponseEntity.ok(productService.updateProduct(id, productDto, brandId));
    }

    // ✅ 6. 상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message", "상품이 삭제되었습니다."));
    }
}
