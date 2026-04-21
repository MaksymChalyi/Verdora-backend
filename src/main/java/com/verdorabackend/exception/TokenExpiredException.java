package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends BaseException {

    public TokenExpiredException() {
        super(HttpStatus.UNAUTHORIZED, "Token expired");
    }
}
