package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class CategoryAlreadyExistsException extends BaseException {

    public CategoryAlreadyExistsException(String name) {
        super(HttpStatus.CONFLICT, "Category already exists, name=" + name);
    }
}
