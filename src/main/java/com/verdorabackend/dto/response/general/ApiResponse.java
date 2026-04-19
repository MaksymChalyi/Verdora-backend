package com.verdorabackend.dto.response.general;

import java.time.OffsetDateTime;

public record ApiResponse<T>(
        OffsetDateTime timestamp,
        int status,
        String message,
        T data
) {
}