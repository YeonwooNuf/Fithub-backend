package com.example.musinsabackend.controller;

import com.example.musinsabackend.model.Product;
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

    // ✅ 1. 전체 상품 조회 (페이지네이션)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Product> productPage = productService.getAllProducts(PageRequest.of(page, size));

        return ResponseEntity.ok(Map.of(
                "products", productPage.getContent(),
                "currentPage", productPage.getNumber(),
                "totalPages", productPage.getTotalPages()
        ));
    }

    // ✅ 2. 특정 상품 조회
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // ✅ 3. 상품 검색 (이름 포함된 상품 조회, 페이지네이션 포함)
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Product> productPage = productService.searchProducts(keyword, PageRequest.of(page, size));

        return ResponseEntity.ok(Map.of(
                "products", productPage.getContent(),
                "currentPage", productPage.getNumber(),
                "totalPages", productPage.getTotalPages()
        ));
    }

    // ✅ 4. 상품 추가 (사이즈, 색상 리스트 포함)
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Map<String, Object> requestData) {
        Long brandId = Long.parseLong(requestData.get("brandId").toString());

        Product product = new Product();
        product.setName((String) requestData.get("name"));
        product.setPrice(Double.valueOf(requestData.get("price").toString()));
        product.setDescription((String) requestData.get("description"));
        product.setImageUrl((String) requestData.get("imageUrl"));

        // 🔥 JSON 리스트 변환 후 저장
        product.setSizes((List<String>) requestData.get("sizes"));
        product.setColors((List<String>) requestData.get("colors"));

        Product createdProduct = productService.addProduct(product, brandId);
        return ResponseEntity.ok(createdProduct);
    }

    // ✅ 5. 상품 수정
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Map<String, Object> requestData
    ) {
        Long brandId = Long.parseLong(requestData.get("brandId").toString());

        Product updatedProduct = new Product();
        updatedProduct.setName((String) requestData.get("name"));
        updatedProduct.setPrice(Double.valueOf(requestData.get("price").toString()));
        updatedProduct.setDescription((String) requestData.get("description"));
        updatedProduct.setImageUrl((String) requestData.get("imageUrl"));

        // 🔥 JSON 리스트 변환 후 저장
        updatedProduct.setSizes((List<String>) requestData.get("sizes"));
        updatedProduct.setColors((List<String>) requestData.get("colors"));

        Product savedProduct = productService.updateProduct(id, updatedProduct, brandId);
        return ResponseEntity.ok(savedProduct);
    }

    // ✅ 6. 상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message", "상품이 삭제되었습니다."));
    }
}
