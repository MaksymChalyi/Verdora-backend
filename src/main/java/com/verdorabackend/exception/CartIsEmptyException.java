package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class CartIsEmptyException extends BaseException {
    public CartIsEmptyException() {
        super(HttpStatus.BAD_REQUEST, "Cannot place order: cart is empty");
    }
}
