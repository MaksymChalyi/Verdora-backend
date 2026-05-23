package com.verdorabackend.mapper;

import com.verdorabackend.dto.response.CartItemResponse;
import com.verdorabackend.dto.response.CartResponse;
import com.verdorabackend.entity.Cart;
import com.verdorabackend.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "id", target = "cartItemId")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.imageUrl", target = "imageUrl")
    @Mapping(source = "product.price", target = "price")
    @Mapping(target = "subtotal", ignore = true)
    CartItemResponse toCartItemResponse(CartItem cartItem);

    @Mapping(source = "id", target = "cartId")
    @Mapping(target = "totalPrice", ignore = true)
    CartResponse toCartResponse(Cart cart);
}
