package com.verdorabackend.service;

import com.verdorabackend.dto.request.CategoryRequest;
import com.verdorabackend.dto.response.CategoryResponse;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest categoryRequest);

    CategoryResponse updateCategory(String categoryId, CategoryRequest categoryRequest);

    void deleteCategory(String categoryId);
}
