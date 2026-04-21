package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class WrongTokenTypeException extends BaseException {

    public WrongTokenTypeException( ) {
        super(HttpStatus.UNAUTHORIZED, "Invalid token type");
    }
}
