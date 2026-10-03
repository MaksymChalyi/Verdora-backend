package com.verdorabackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Profile update request")
public record UpdateUserRequest(

        @Schema(
                description = "User name",
                example = "Stepan"
        )
        @NotBlank(message = "Name is required")
        @Size(max = 256, message = "Name must not exceed 256 characters")
        String name,

        @Schema(
                description = "Phone number in international format",
                example = "+380989703417"
        )
        @Pattern(
                regexp = "^\\+[1-9]\\d{7,14}$",
                message = "Invalid phone number format"
        )
        String phone

) {
}
