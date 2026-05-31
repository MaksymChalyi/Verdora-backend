package com.verdorabackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Registration request payload")
public record SignUpRequest(

        @Schema(description = "name", example = "Stepan")
        @NotBlank(message = "Name is required")
        @Size(min = 2, message = "Name must be at least 2 characters")
        String name,

        @Schema(description = "email", example = "user@gmail.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @Schema(description = "phone", example = "+380989703417")
        String phone,

        @Schema(description = "password", example = "StrongPass1!")
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%]).+$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character (!@#$%)"
        )
        String password
) {
}
