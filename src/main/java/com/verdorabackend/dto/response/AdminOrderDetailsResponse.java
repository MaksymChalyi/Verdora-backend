package com.verdorabackend.dto.response;

import com.verdorabackend.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "Detailed order response for admin")
public record AdminOrderDetailsResponse(

        @Schema(description = "Order ID", example = "1")
        Long orderId,

        @Schema(description = "Order creation timestamp", example = "2026-05-23T12:00:00Z")
        OffsetDateTime createdAt,

        @Schema(description = "Total price of the order", example = "1500.00")
        BigDecimal totalPrice,

        @Schema(description = "Order status", example = "PENDING")
        OrderStatus status,

        @Schema(description = "Customer")
        UserResponse customer,

        @Schema(description = "Ordered items")
        List<OrderItemResponse> items
) {
}
