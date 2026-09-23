package com.verdorabackend.dto.response;

import com.verdorabackend.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "Order response for admin")
public record AdminOrderResponse(

        @Schema(description = "Order ID", example = "1")
        Long orderId,

        @Schema(description = "Order creation timestamp", example = "2026-05-23T12:00:00Z")
        OffsetDateTime createdAt,

        @Schema(description = "Customer")
        UserResponse customer,

        @Schema(description = "Total price of the order", example = "1500.00")
        BigDecimal totalPrice,

        @Schema(description = "Order status", example = "PENDING")
        OrderStatus status
) {
}
