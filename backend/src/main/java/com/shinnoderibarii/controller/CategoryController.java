package com.shinnoderibarii.controller;

import com.shinnoderibarii.entity.Category;
import com.shinnoderibarii.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores/{storeId}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<Category> getCategories(@PathVariable Long storeId) {
        return categoryService.getCategoriesByStoreId(storeId);
    }
}