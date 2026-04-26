package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends BaseException {
    public CategoryNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Category not found");
    }
}
