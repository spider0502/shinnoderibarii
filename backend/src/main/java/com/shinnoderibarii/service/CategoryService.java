package com.shinnoderibarii.service;

import com.shinnoderibarii.entity.Category;
import com.shinnoderibarii.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public List<Category> getCategoriesByStoreId(Long storeId) {
        return categoryMapper.findByStoreId(storeId);
    }
}