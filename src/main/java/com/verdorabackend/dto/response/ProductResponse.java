package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "Product response payload")
public record ProductResponse(

        @Schema(description = "Product ID", example = "1")
        Long productId,

        @Schema(description = "Product name", example = "Laptop")
        String name,

        @Schema(description = "Product description",
                example = "High-performance gaming laptop with 16GB RAM")
        String description,

        @Schema(description = "Product price", example = "500")
        BigDecimal price,

        @Schema(description = "Category ID", example = "1")
        Long categoryId,

        @Schema(description = "Product image", example = "https://site.com/image.jpg")
        String imageUrl,

        @Schema(description = "Product discount price", example = "300")
        BigDecimal discountPrice,

        @Schema(
                description = "Product creation timestamp",
                example = "2026-05-03T12:00:00Z"
        )
        OffsetDateTime createdAt,

        @Schema(
                description = "Product last update timestamp",
                example = "2026-05-03T12:10:00Z"
        )
        OffsetDateTime updatedAt
) {
}
