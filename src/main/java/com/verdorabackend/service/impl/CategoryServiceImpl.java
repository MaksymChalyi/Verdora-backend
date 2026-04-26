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
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        log.debug("Creating category with name: {}", categoryRequest.name());

        Category category = categoryMapper.toEntity(categoryRequest);


        Category saved = categoryRepository.save(category);

        log.info("Category created successfully: id={}, name={}",
                saved.getId(), saved.getName());

        return categoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(String categoryId, CategoryRequest categoryRequest) {
       log.debug("Updating category if={}", categoryId);

       Long id = Long.parseLong(categoryId);

       Category category = categoryRepository.findById(id)
               .orElseThrow(() -> {
                   log.warn("Category not found for update, id={}", categoryId);
                   return new CategoryNotFoundException();
               });

       category.setName(categoryRequest.name());

       Category updated = categoryRepository.save(category);

       log.info("Category updated successfully: id={}", updated.getId());

       return categoryMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCategory(String categoryId) {

        log.debug("Deleting category id={}", categoryId);

        Long id = Long.parseLong(categoryId);

        if (!categoryRepository.existsById(id)) {
            log.warn("Category not found for delete, id={}", categoryId);
            throw new CategoryNotFoundException();
        }

        categoryRepository.deleteById(id);

        log.info("Category deleted successfully: id={}", categoryId);
    }
}
