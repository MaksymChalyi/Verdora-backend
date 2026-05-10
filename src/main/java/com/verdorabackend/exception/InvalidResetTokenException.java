package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class InvalidResetTokenException extends BaseException {

    public InvalidResetTokenException() {
        super(HttpStatus.BAD_REQUEST, "Invalid reset token");
    }
}
