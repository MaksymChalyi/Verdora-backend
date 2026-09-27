package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class ProductDeletionException extends BaseException {

    public ProductDeletionException(Long productId) {
        super(HttpStatus.CONFLICT,
                "Product cannot be deleted because it is currently in use, id=" + productId);
    }
}
