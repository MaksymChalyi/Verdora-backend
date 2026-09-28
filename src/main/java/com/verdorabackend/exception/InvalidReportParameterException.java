package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class InvalidReportParameterException extends BaseException {

    public InvalidReportParameterException() {
        this("Parameter 'n' must be a positive integer");
    }

    public InvalidReportParameterException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
