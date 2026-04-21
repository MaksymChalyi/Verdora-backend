package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BaseException {

    public UserAlreadyExistsException() {
        super(HttpStatus.BAD_REQUEST, "Email already exists");
    }
}
