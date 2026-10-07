package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Favorite state")
public record FavoriteStateResponse(

        @Schema(description = "Product ID", example = "1")
        Long productId,

        @Schema(
                description = "Whether the product is currently in favorites",
                example = "true"
        )
        boolean favorite

) {
}
