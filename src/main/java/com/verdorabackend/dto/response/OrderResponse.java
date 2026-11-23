package com.verdorabackend.dto.response;

import com.verdorabackend.entity.DeliveryMethod;
import com.verdorabackend.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "Order details")
public record OrderResponse(

        @Schema(description = "Order ID", example = "1")
        Long orderId,

        @Schema(description = "Order creation timestamp", example = "2026-05-23T12:00:00Z")
        OffsetDateTime createdAt,

        @Schema(description = "Total price of the order (items + shipping)", example = "1650.00")
        BigDecimal totalPrice,

        @Schema(description = "Order status", example = "PENDING")
        OrderStatus status,

        @Schema(description = "List of ordered items")
        List<OrderItemResponse> items,

        @Schema(description = "Sum of all items without shipping", example = "1500.00")
        BigDecimal itemsTotal,

        @Schema(description = "Shipping cost", example = "150.00")
        BigDecimal shippingCost,

        @Schema(description = "Delivery method (null for legacy orders)", example = "COURIER_DELIVERY")
        DeliveryMethod deliveryMethod,

        @Schema(description = "Delivery address (COURIER_DELIVERY only)", example = "Kyiv, Khreshchatyk St, 22, apt. 5")
        String deliveryAddress,

        @Schema(description = "Pickup point ID (STORE_PICKUP only)", example = "1")
        Long pickupPointId
) {
}
