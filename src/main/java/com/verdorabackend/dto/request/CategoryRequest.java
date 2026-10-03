package com.verdorabackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Category request payload")
public record CategoryRequest(

        @Schema(description = "Category name", example = "Electronics")
        @NotBlank(message = "Category name is required")
        @Size(max = 256, message = "Category name must not exceed 256 characters")
        String name,

        @Schema(description = "Category image URL", example = "https://example.com/categories/electronics.jpg")
        @NotBlank(message = "Image is required")
        @Size(max = 2048, message = "Image URL must not exceed 2048 characters")
        String imageUrl
) {
}
