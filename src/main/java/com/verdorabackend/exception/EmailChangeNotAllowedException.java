package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class EmailChangeNotAllowedException extends BaseException {

    public EmailChangeNotAllowedException() {
        super(HttpStatus.BAD_REQUEST, "Email cannot be changed");
    }
}
