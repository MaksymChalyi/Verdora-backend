package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class ResetTokenExpiredException extends BaseException {
    public ResetTokenExpiredException() {
        super(HttpStatus.BAD_REQUEST, "Reset token expired");
    }
}
