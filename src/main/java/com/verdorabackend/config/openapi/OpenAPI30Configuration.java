package com.verdorabackend.config.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Verdora API",
                version = "1.0",
                description = "Authentication and user management API with JWT cookie-based authentication"
        )
)
public class OpenAPI30Configuration {
}