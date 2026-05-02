package com.verdorabackend.service.impl;

import com.verdorabackend.dto.request.CategoryRequest;
import com.verdorabackend.dto.response.CategoryResponse;
import com.verdorabackend.entity.Category;
import com.verdorabackend.exception.CategoryNotFoundException;
import com.verdorabackend.mapper.CategoryMapper;
import com.verdorabackend.repository.CategoryRepository;
import com.verdorabackend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        log.debug("Creating category with name: {}", request.name());
        Category category = categoryMapper.toEntity(request);
        Category saved = categoryRepository.save(category);
        log.info("Category created, id={}", saved.getId());
        return categoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = getByIdOrThrow(id);
        category.setName(request.name());
        log.info("Category updated, id={}", id);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getByIdOrThrow(id);
        categoryRepository.delete(category);
        log.info("Category deleted, id={}", id);
    }

    private Category getByIdOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }
}
