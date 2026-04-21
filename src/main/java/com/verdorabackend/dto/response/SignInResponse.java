package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response after successful login")
public record SignInResponse(

        @Schema(description = "User email", example = "user@gmail.com")
        String email
) {
}
