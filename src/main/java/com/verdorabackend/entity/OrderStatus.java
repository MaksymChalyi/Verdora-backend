package com.verdorabackend.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Order status values:
        PENDING - Pending payment,
        PAID - Confirmed,
        SHIPPED - In Transit,
        DELIVERED - Delivered,
        CANCELLED - Cancelled
        """)
public enum OrderStatus {
    PENDING,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == PAID || newStatus == CANCELLED;
            case PAID -> newStatus == SHIPPED || newStatus == CANCELLED;
            case SHIPPED -> newStatus == DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }

    public boolean isFinal() {
        return this == DELIVERED || this == CANCELLED;
    }
}
