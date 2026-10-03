package com.verdorabackend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DeleteAccountRequest(
        @NotBlank(message = "Password is required")
        String password
) {
}