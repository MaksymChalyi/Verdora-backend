package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends BaseException {
    public ProductNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "Product not found, id=" + id);
    }
}
