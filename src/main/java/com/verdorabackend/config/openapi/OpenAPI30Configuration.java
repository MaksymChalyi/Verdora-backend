package com.verdorabackend.config.openapi;

import com.verdorabackend.security.UserPrincipal;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Verdora API",
                version = "1.0",
                description = "Authentication and user management API with JWT cookie-based authentication"
        )
)
@SecurityScheme(
        name = "Cookie-based Authentication",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.COOKIE,
        paramName = "accessToken"
)
public class OpenAPI30Configuration {

        static {
                SpringDocUtils.getConfig().addRequestWrapperToIgnore(UserPrincipal.class);
        }
}