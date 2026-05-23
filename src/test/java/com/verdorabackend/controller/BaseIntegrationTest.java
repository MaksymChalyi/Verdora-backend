package com.verdorabackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.verdorabackend.TestSecurityConfig;
import com.verdorabackend.entity.Role;
import com.verdorabackend.entity.User;
import com.verdorabackend.security.JwtService;
import com.verdorabackend.security.UserPrincipal;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtService jwtService;

    protected Cookie authCookie(Long userId, String email, Role role) {
        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setPasswordHash("$2a$10$2Y8OeOw8hHSbi.Oihi43du7ie0E5OtKgBOMlVmSNObfn42B2FhkR.");
        user.setName("Test");
        user.setRole(role);

        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtService.generateAccessToken(principal);
        return new Cookie("accessToken", token);
    }

    // userId=1 → admin@verdora.com (V9 migration)
    protected Cookie adminCookie() {
        return authCookie(1L, "admin@verdora.com", Role.ADMIN);
    }

    // userId=2 → ivan@verdora.com (V11 migration)
    protected Cookie userCookie() {
        return authCookie(2L, "ivan@verdora.com", Role.USER);
    }
}
