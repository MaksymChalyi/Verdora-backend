package com.verdorabackend.dto.response;

import com.verdorabackend.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "Order details")
public record OrderResponse(

        @Schema(description = "Order ID", example = "1")
        Long orderId,

        @Schema(description = "Order status", example = "PENDING")
        OrderStatus status,

        @Schema(description = "Total price of the order", example = "1500.00")
        BigDecimal totalPrice,

        @Schema(description = "List of ordered items")
        List<OrderItemResponse> items,

        @Schema(description = "Order creation timestamp", example = "2026-05-23T12:00:00Z")
        OffsetDateTime createdAt
) {
}
