package com.verdorabackend.dto.request;

import com.verdorabackend.validation.ValidDiscountPrice;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@ValidDiscountPrice
@Schema(description = "Product request payload")
public record ProductRequest(

        @Schema(description = "Product name", example = "Laptop")
        @NotBlank(message = "Name is required")
        @Size(max = 256)
        String name,

        @Schema(
                description = "Product description",
                example = "High-performance gaming laptop with 16GB RAM"
        )
        @Size(max = 1000)
        String description,

        @Schema(description = "Product price", example = "500")
        @NotNull(message = "Price is required")
        @PositiveOrZero
        BigDecimal price,

        @Schema(description = "Category ID", example = "1")
        @NotNull(message = "Category is required")
        Long categoryId,

        @Schema(description = "Product image", example = "https://site.com/image.jpg")
        @NotBlank(message = "Image is required")
        String imageUrl,

        @Schema(description = "Product discount price", example = "300")
        @PositiveOrZero
        BigDecimal discountPrice
) {

}
