package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Top cancelled product report item")
public record TopCancelledProductResponse(
        @Schema(description = "Product ID", example = "1")
        Long productId,

        @Schema(description = "Product name", example = "Laptop")
        String productName,

        @Schema(description = "Total cancelled quantity", example = "15")
        Long cancelledQuantity
) {
}
