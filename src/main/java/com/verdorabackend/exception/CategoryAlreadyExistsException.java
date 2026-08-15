package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class CategoryAlreadyExistsException extends BaseException {

    public CategoryAlreadyExistsException(String name) {
        super(HttpStatus.CONFLICT, "Category with name '%s' already exists".formatted(name));
    }

}