package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Checkout price breakdown calculated before the order is placed")
public record CheckoutSummaryResponse(

        @Schema(description = "Sum of all cart items", example = "1500.00")
        BigDecimal itemsTotal,

        @Schema(description = "Shipping cost", example = "150.00")
        BigDecimal shippingCost,

        @Schema(description = "Items total + shipping cost", example = "1650.00")
        BigDecimal totalPrice
) {
}
