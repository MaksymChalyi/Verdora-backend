package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class FavoriteNotFoundException extends BaseException {
    public FavoriteNotFoundException(Long productId) {
        super(HttpStatus.NOT_FOUND, "Product not found in favorites, productId=" + productId);
    }
}
