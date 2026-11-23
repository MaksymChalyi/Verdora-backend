package com.verdorabackend.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
        Delivery method values:
        COURIER_DELIVERY - Courier Delivery to the customer address,
        STORE_PICKUP - Store Pickup from one of the pickup points
        """)
public enum DeliveryMethod {
    COURIER_DELIVERY,
    STORE_PICKUP
}
