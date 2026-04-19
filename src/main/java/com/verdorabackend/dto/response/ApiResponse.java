package com.verdorabackend.dto.response;

import java.time.OffsetDateTime;

public record ApiResponse<T>(
        OffsetDateTime timestamp,
        int status,
        String message,
        T data
) {
}