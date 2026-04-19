package com.verdorabackend.exception.handler;

import com.verdorabackend.dto.response.ApiResponse;
import com.verdorabackend.dto.response.ApiResponseFactory;
import com.verdorabackend.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(ApiResponseFactory.error(
                        exception.getStatus(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseFactory.error(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Internal server error"
                ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseFactory.error(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                ));
    }

}
