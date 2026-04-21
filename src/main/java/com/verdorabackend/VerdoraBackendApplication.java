package com.verdorabackend;

import com.verdorabackend.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(JwtProperties.class)
@SpringBootApplication
public class VerdoraBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(VerdoraBackendApplication.class, args);
    }

}
