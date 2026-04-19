package com.verdorabackend.exception.handler;

import com.verdorabackend.dto.response.ApiResponse;
import com.verdorabackend.dto.response.ApiResponseFactory;
import com.verdorabackend.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ApiResponse<Void> handleApiException(ApiException exception) {
        return ApiResponseFactory.error(
                exception.getStatus(),
                exception.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleGeneric(Exception exception) {
        log.error("Unhandled exception", exception);
        return ApiResponseFactory.error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error"
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ApiResponse<Void> handleBadCredentials(BadCredentialsException exception) {
        return ApiResponseFactory.error(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password"
        );
    }

}
