package com.verdorabackend.dto.response.general;

import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public class ApiResponseFactory {

    public static <T> ApiResponse<T> success(HttpStatus status, String message, T data) {
        return new ApiResponse<>(
                OffsetDateTime.now(),
                status.value(),
                message,
                data
        );
    }

    public static ApiResponse<Void> success(HttpStatus status, String message) {
        return new ApiResponse<>(
                OffsetDateTime.now(),
                status.value(),
                message,
                null
        );
    }

    public static ApiResponse<Void> error(HttpStatus status, String message) {
        return new ApiResponse<>(
                OffsetDateTime.now(),
                status.value(),
                message,
                null
        );
    }
}
