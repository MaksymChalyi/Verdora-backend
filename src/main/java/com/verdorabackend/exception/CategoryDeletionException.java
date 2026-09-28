package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class CategoryDeletionException extends BaseException {

    public CategoryDeletionException(Long categoryId) {
        super(HttpStatus.CONFLICT, "Category cannot be deleted because it is currently in use, id=" + categoryId);
    }
}