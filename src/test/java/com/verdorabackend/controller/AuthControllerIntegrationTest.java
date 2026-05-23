package com.verdorabackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class AuthControllerIntegrationTest extends BaseIntegrationTest {

    // ── POST /auth/login ──────────────────────────────────────────────────────

    @Test
    void login_validCredentials_returns200WithCookie() throws Exception {
        String body = """
                {
                  "email": "admin@verdora.com",
                  "password": "password"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(jsonPath("$.data.email").value("admin@verdora.com"));
    }

    @Test
    void login_wrongPassword_returns401() throws Exception {
        String body = """
                {
                  "email": "admin@verdora.com",
                  "password": "wrongpassword"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /auth/logout ─────────────────────────────────────────────────────

    @Test
    void logout_authenticated_returns200AndClearsCookies() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("accessToken", 0))
                .andExpect(cookie().maxAge("refreshToken", 0));
    }

    // ── POST /auth/forgot-password ────────────────────────────────────────────

    @Test
    void forgotPassword_existingEmail_returns200() throws Exception {
        String body = """
                {
                  "email": "admin@verdora.com"
                }
                """;

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

}
