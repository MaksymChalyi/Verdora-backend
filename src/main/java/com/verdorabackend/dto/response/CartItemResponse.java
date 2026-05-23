package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Single item in the cart")
public record CartItemResponse(

        @Schema(description = "Cart item ID", example = "1")
        Long cartItemId,

        @Schema(description = "Product ID", example = "5")
        Long productId,

        @Schema(description = "Product name", example = "Laptop")
        String productName,

        @Schema(description = "Product image URL", example = "https://site.com/image.jpg")
        String imageUrl,

        @Schema(description = "Current product price", example = "500.00")
        BigDecimal price,

        @Schema(description = "Quantity in cart", example = "2")
        Long quantity,

        @Schema(description = "Price × quantity", example = "1000.00")
        BigDecimal subtotal
) {
}
