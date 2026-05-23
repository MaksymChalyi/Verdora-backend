package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class OrderCannotBeCancelledException extends BaseException {
    public OrderCannotBeCancelledException(Long id) {
        super(HttpStatus.CONFLICT, "Order cannot be cancelled, id=" + id + ". Only PENDING orders can be cancelled");
    }
}
