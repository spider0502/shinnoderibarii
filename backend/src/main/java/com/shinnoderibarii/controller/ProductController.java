package com.shinnoderibarii.controller;

import com.shinnoderibarii.entity.Product;
import com.shinnoderibarii.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores/{storeId}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<Product> getProducts(@PathVariable Long storeId) {
        return productService.getProductsByStoreId(storeId);
    }
}