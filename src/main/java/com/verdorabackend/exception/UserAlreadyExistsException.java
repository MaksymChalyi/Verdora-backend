package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends ApiException {

    public UserAlreadyExistsException() {
        super(HttpStatus.BAD_REQUEST, "Email already exists");
    }
}
