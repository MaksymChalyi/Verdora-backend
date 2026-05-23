package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class CartItemNotFoundException extends BaseException {
    public CartItemNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "Cart item not found, id=" + id);
    }
}
