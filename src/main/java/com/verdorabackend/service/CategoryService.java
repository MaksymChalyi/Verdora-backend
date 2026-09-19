package com.verdorabackend.service;

import com.verdorabackend.dto.request.CategoryRequest;
import com.verdorabackend.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest categoryRequest);

    CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest);

    void deleteCategory(Long categoryId);

    Page<CategoryResponse> getAllCategories(Pageable pageable);

    List<CategoryResponse> getAllCategoriesForAdmin();

    CategoryResponse getCategory(Long categoryId);
}
