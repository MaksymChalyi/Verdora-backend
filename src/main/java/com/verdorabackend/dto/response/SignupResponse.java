package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignupResponse(
        @Schema(description = "Email of the successfully registered user")
        String email) {
}
