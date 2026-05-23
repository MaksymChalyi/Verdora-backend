package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends BaseException {
    public OrderNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "Order not found, id=" + id);
    }
}
