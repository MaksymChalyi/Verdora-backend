package com.verdorabackend.mapper;

import com.verdorabackend.dto.request.ProductRequest;
import com.verdorabackend.dto.response.ProductResponse;
import com.verdorabackend.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductRequest request);

    @Mapping(source = "id", target = "productId")
    @Mapping(source = "category.id", target = "categoryId")
    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    void updateProductFromRequest(
            ProductRequest request,
            @MappingTarget Product product
    );
}
