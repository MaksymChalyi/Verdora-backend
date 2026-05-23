package com.verdorabackend.mapper;

import com.verdorabackend.dto.response.FavoriteResponse;
import com.verdorabackend.entity.Favorite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.imageUrl", target = "imageUrl")
    @Mapping(source = "product.price", target = "price")
    @Mapping(source = "product.discountPrice", target = "discountPrice")
    @Mapping(source = "createdAt", target = "addedAt")
    FavoriteResponse toResponse(Favorite favorite);
}
