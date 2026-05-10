package com.verdorabackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(

        @Schema(description = "Password reset token from email", example = "bf8d94df-4a90-4858-8ed9-7d4cc9deed26")
        @NotBlank
        String token,

        @Schema(description = "New password", example = "NewPass1!")
        @NotBlank
        @Size(min = 8, max = 20)
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%]).+$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character (!@#$%)"
        )
        String newPassword
) {
}
