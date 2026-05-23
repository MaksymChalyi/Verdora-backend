package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "Favorite product")
public record FavoriteResponse(

        @Schema(description = "Product ID", example = "1")
        Long productId,

        @Schema(description = "Product name", example = "Ноутбук ASUS VivoBook 15")
        String productName,

        @Schema(description = "Product image URL", example = "https://picsum.photos/seed/p1/400/300")
        String imageUrl,

        @Schema(description = "Product price", example = "32999.99")
        BigDecimal price,

        @Schema(description = "Product discount price", example = "27999.99")
        BigDecimal discountPrice,

        @Schema(description = "Date added to favorites", example = "2026-05-23T12:00:00Z")
        OffsetDateTime addedAt
) {
}
