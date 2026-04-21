package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response after successful registration")
public record SignupResponse(

        @Schema(description = "Registration user email", example ="user@gmail.com" )
        String email
) {
}
