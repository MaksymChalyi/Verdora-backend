package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User response payload")
public record UserResponse(

        @Schema(description = "User ID", example = "1")
        Long id,

        @Schema(description = "User name", example = "Stepan")
        String name,

        @Schema(description = "User email", example = "stepan@gmail.com")
        String email,

        @Schema(description = "User phone", example = "+380989703417")
        String phone
) {
}
