package com.verdorabackend.mapper;

import com.verdorabackend.dto.response.OrderItemResponse;
import com.verdorabackend.dto.response.OrderResponse;
import com.verdorabackend.entity.Order;
import com.verdorabackend.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "id", target = "orderItemId")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(target = "subtotal", ignore = true)
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    @Mapping(source = "id", target = "orderId")
    OrderResponse toOrderResponse(Order order);
}
