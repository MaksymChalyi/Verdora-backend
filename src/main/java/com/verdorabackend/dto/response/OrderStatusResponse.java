package com.verdorabackend.dto.response;

import com.verdorabackend.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Order status response")
public record OrderStatusResponse(

        @Schema(description = "Order ID", example = "15")
        Long orderId,

        @Schema(description = "Current order status", example = "SHIPPED")
        OrderStatus status,

        @Schema(description = "Indicates whether the order has reached a final status", example = "false")
        boolean finalStatus
) {
}
