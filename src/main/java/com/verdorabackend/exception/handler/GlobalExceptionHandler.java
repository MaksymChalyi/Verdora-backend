package com.verdorabackend.exception.handler;

import com.verdorabackend.dto.response.BaseResponse;
import com.verdorabackend.dto.response.BaseResponseFactory;
import com.verdorabackend.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<Void>> handleApiException(BaseException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(BaseResponseFactory.error(
                        exception.getStatus(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler({
            BadCredentialsException.class,
            UsernameNotFoundException.class
    })
    public ResponseEntity<BaseResponse<Void>> handleAuthException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(BaseResponseFactory.error(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidationException(
            MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Validation failed");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponseFactory.error(
                        HttpStatus.BAD_REQUEST,
                        message
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<Void>> handleMalformedJson(
            HttpMessageNotReadableException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponseFactory.error(
                        HttpStatus.BAD_REQUEST,
                        "Malformed JSON request"
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Void>> handleAccessDenied(
            AccessDeniedException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(BaseResponseFactory.error(
                        HttpStatus.FORBIDDEN,
                        "Forbidden"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGeneric(Exception exception) {
        log.error("Unhandled exception", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponseFactory.error(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Internal server error"
                ));
    }

}
