package com.verdorabackend.mapper;

import com.verdorabackend.dto.request.CategoryRequest;
import com.verdorabackend.dto.response.CategoryResponse;
import com.verdorabackend.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryRequest request);

    @Mapping(source = "id", target = "categoryId")
    @Mapping(source = "name", target = "name")
    CategoryResponse toResponse(Category category);
}
