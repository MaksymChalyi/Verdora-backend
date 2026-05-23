package com.verdorabackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request to update cart item quantity")
public record UpdateCartItemRequest(

        @Schema(description = "New quantity for the cart item", example = "3")
        @NotNull
        @Positive
        Long quantity
) {
}
