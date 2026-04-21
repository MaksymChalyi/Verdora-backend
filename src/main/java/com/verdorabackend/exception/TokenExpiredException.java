package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends ApiException {

    public TokenExpiredException() {
        super(HttpStatus.UNAUTHORIZED, "Token expired");
    }
}
