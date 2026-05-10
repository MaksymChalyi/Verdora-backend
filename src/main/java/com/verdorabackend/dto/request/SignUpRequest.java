package com.verdorabackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Registration request payload")
public record SignUpRequest(

        @Schema(description = "name", example = "Stepan")
        String name,

        @Schema(description = "email", example = "user@gmail.com")
        String email,

        @Schema(description = "phone", example = "+380989703417")
        String phone,

        @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%]).+$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character (!@#$%)"
        )
        @Schema(description = "password", example = "12345678")
        String password
) {
}
