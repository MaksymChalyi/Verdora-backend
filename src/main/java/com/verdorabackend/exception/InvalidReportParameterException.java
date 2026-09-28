package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class InvalidReportParameterException extends BaseException {

    public InvalidReportParameterException() {
        super(HttpStatus.BAD_REQUEST, "Parameter 'n' must be a positive integer");
    }
}
