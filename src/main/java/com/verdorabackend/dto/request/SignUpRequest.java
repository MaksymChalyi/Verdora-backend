package com.verdorabackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record SignUpRequest(

        @Schema(description = "name", example = "Stepan")
        String name,

        @Schema(description = "email", example = "user@gmail.com")
        String email,

        @Schema(description = "phone", example = "+380989703417")
        String phone,

        @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
        @Schema(description = "password", example = "12345678")
        String password
) {
}

