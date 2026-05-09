package com.verdorabackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Product request payload")
public record ProductRequest(

        @Schema(description = "Product name", example = "Laptop")
        @NotBlank
        @Size(max = 256)
        String name,

        @Schema(description = "Product description",
                example = "High-performance gaming laptop with 16GB RAM")
        @NotBlank
        @Size(max = 1000)
        String description,

        @Schema(description = "Product price", example = "500")
        @NotNull
        @PositiveOrZero
        BigDecimal price,

        @Schema(description = "Category ID", example = "1")
        @NotNull
        Long categoryId,

        @Schema(description = "Product image", example = "https://site.com/image.jpg")
        @NotBlank
        String imageUrl,

        @Schema(description = "Product discount price", example = "300")
        @NotNull
        BigDecimal discountPrice
) {

}
