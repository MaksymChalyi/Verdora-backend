package com.verdorabackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(

        @Schema(description = "User email address", example = "user@gmail.com")
        @NotBlank
        @Email
        String email
) {
}
