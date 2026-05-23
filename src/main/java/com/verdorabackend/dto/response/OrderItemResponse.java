package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Single item in an order")
public record OrderItemResponse(

        @Schema(description = "Order item ID", example = "1")
        Long orderItemId,

        @Schema(description = "Product ID", example = "5")
        Long productId,

        @Schema(description = "Product name at time of purchase", example = "Laptop")
        String productName,

        @Schema(description = "Quantity ordered", example = "2")
        Integer quantity,

        @Schema(description = "Price at time of purchase", example = "500.00")
        BigDecimal priceAtPurchase,

        @Schema(description = "Subtotal for this item", example = "1000.00")
        BigDecimal subtotal
) {
}
