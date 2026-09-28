package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "Pending payment order report item")
public record PendingPaymentOrderResponse(
        @Schema(description = "Order ID", example = "1")
        Long orderId,

        @Schema(description = "Order creation timestamp", example = "2026-09-20T12:00:00Z")
        OffsetDateTime createdAt,

        @Schema(description = "Total order price", example = "1500.00")
        BigDecimal totalPrice,

        @Schema(description = "Number of days the order has been pending", example = "8")
        long pendingDays,

        @Schema(description = "Customer")
        UserResponse customer
) {
}
