package com.verdorabackend.dto.request;

import com.verdorabackend.entity.DeliveryMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = """
        Checkout delivery details.
        COURIER_DELIVERY requires deliveryAddress; STORE_PICKUP requires pickupPointId.
        """)
public record CheckoutRequest(

        @Schema(description = "Delivery method", example = "COURIER_DELIVERY", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Delivery method is required")
        DeliveryMethod deliveryMethod,

        @Schema(description = "Delivery address (required for COURIER_DELIVERY)", example = "Kyiv, Khreshchatyk St, 22, apt. 5")
        @Size(max = 512, message = "Delivery address must be at most 512 characters")
        String deliveryAddress,

        @Schema(description = "Pickup point ID (required for STORE_PICKUP)", example = "1")
        @Positive(message = "Pickup point ID must be positive")
        Long pickupPointId
) {
}
