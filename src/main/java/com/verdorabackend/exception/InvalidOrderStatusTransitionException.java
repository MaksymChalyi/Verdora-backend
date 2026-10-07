package com.verdorabackend.exception;

import com.verdorabackend.entity.OrderStatus;
import org.springframework.http.HttpStatus;

public class InvalidOrderStatusTransitionException extends BaseException {

    public InvalidOrderStatusTransitionException(OrderStatus currentStatus, OrderStatus newStatus) {
        super(HttpStatus.CONFLICT,
                "Invalid order status transition: "
                        + currentStatus
                        + " -> "
                        + newStatus
        );
    }
}
