package com.verdorabackend.exception;

import com.verdorabackend.entity.OrderStatus;
import org.springframework.http.HttpStatus;

public class OrderCannotBeCancelledException extends BaseException {

    public OrderCannotBeCancelledException(Long id, OrderStatus status) {
        super(HttpStatus.CONFLICT,
                "Order cannot be cancelled, id=" + id
                        + ". Current status: " + status
                        + ". Only PENDING and PAID orders can be cancelled"
        );
    }

    public OrderCannotBeCancelledException(Long id) {
        super(HttpStatus.CONFLICT, "Order is already cancelled, id=" + id);
    }
}
