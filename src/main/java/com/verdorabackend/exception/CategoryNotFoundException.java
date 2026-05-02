package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends BaseException {
    public CategoryNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "Category not found, id=" + id);
    }
}
