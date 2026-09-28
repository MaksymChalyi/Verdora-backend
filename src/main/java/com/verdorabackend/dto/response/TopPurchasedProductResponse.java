package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Top purchased product report item")
public record TopPurchasedProductResponse(
        @Schema(description = "Product ID", example = "1")
        Long productId,

        @Schema(description = "Product name", example = "Laptop")
        String productName,

        @Schema(description = "Total purchased quantity", example = "25")
        Long purchasedQuantity
) {
}
