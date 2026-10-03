package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Order item response")
public record OrderItemResponse(

        @Schema(description = "Order item ID", example = "1")
        Long orderItemId,

        @Schema(description = "Product ID", example = "1")
        Long productId,

        @Schema(description = "Product name", example = "Laptop")
        String productName,

        @Schema(description = "Product image URL", example = "https://example.com/products/laptop.jpg")
        String imageUrl,

        @Schema(description = "Product category name", example = "Electronics")
        String categoryName,

        @Schema(description = "Quantity", example = "2")
        Integer quantity,

        @Schema(description = "Product price at the moment of purchase", example = "800.00")
        BigDecimal priceAtPurchase,

        @Schema(description = "Item subtotal", example = "1600.00")
        BigDecimal subtotal
) {
}
