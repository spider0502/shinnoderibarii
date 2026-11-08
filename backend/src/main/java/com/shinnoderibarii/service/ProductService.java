package com.shinnoderibarii.service;

import com.shinnoderibarii.entity.Product;
import com.shinnoderibarii.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    public List<Product> getProductsByStoreId(Long storeId) {
        return productMapper.findByStoreId(storeId);
    }
}