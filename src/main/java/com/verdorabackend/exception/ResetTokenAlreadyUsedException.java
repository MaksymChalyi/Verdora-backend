package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class ResetTokenAlreadyUsedException extends BaseException {
    public ResetTokenAlreadyUsedException() {
        super(HttpStatus.BAD_REQUEST, "Reset token already used");
    }
}
