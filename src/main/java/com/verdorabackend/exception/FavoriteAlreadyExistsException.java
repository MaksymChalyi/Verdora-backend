package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class FavoriteAlreadyExistsException extends BaseException {
    public FavoriteAlreadyExistsException(Long productId) {
        super(HttpStatus.CONFLICT, "Product already in favorites, productId=" + productId);
    }
}
