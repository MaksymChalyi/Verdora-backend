package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Standard API response wrapper used in all endpoints")
public record BaseResponse<T>(
        @Schema(description = "Response timestamp")
        OffsetDateTime timestamp,

        @Schema(description = "HTTP status code", example = "200")
        int status,

        @Schema(description = "Response message", example = "Success")
        String message,

        @Schema(description = "Response payload")
        T data
) {
}