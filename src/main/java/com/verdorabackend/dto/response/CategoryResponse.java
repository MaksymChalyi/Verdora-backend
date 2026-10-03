package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryResponse(

        @Schema(description = "Category ID", example = "1")
        Long categoryId,

        @Schema(description = "Category name", example = "Electronics")
        String name,

        @Schema(
                description = "Category image URL",
                example = "https://example.com/categories/electronics.jpg"
        )
        String imageUrl

) {
}