package com.verdorabackend.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.oauth2")
@Getter
@Setter
public class OAuth2Properties {

    private String redirectUri;
}
