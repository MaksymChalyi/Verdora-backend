package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Cart with all items and total price")
public record CartResponse(

        @Schema(description = "Cart ID", example = "1")
        Long cartId,

        @Schema(description = "List of cart items")
        List<CartItemResponse> items,

        @Schema(description = "Total price of all items", example = "1500.00")
        BigDecimal totalPrice
) {
}
