package com.verdorabackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Update user request payload")
public record UpdateUserRequest(

        @Schema(description = "name", example = "Stepan")
        @NotBlank
        String name,

        @Schema(description = "phone", example = "+380989703417")
        @NotBlank
        String phone
) {
}
