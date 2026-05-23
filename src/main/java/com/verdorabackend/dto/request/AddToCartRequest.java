package com.verdorabackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request to add a product to the cart")
public record AddToCartRequest(

        @Schema(description = "Product ID to add", example = "1")
        @NotNull
        Long productId,

        @Schema(description = "Quantity of the product", example = "2")
        @NotNull
        @Positive
        Long quantity
) {
}
