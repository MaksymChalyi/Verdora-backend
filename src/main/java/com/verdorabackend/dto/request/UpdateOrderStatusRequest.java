package com.verdorabackend.dto.request;

import com.verdorabackend.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to update order status (ADMIN only)")
public record UpdateOrderStatusRequest(

        @Schema(description = "New order status", example = "SHIPPED")
        @NotNull
        OrderStatus status
) {
}
