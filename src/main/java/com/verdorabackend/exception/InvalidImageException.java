package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class InvalidImageException extends BaseException {

    public InvalidImageException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
