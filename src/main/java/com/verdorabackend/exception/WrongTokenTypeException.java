package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class WrongTokenTypeException extends ApiException {

    public WrongTokenTypeException( ) {
        super(HttpStatus.UNAUTHORIZED, "Invalid token type");
    }
}
