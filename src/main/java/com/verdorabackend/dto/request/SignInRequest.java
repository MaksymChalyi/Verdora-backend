package com.verdorabackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login request payload")
public record SignInRequest(

        @Schema(description = "User email", example = "user@gmail.com")
        @Email
        @NotBlank
        String email,

        @Schema(description = "password", example = "12345678")
        @NotBlank
        String password
) {
}
