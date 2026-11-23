package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class InvalidDeliveryDetailsException extends BaseException {
    public InvalidDeliveryDetailsException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
