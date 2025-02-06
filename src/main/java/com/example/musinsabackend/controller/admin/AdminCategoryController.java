package com.example.musinsabackend.controller.admin;

import com.example.musinsabackend.model.ProductCategory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    // ✅ 카테고리 목록 반환
    @GetMapping
    public List<String> getAllCategories() {
        return Arrays.stream(ProductCategory.values())
                .map(Enum::name)
                .toList();
    }
}
