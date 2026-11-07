package com.verdorabackend.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                  "password": "Password1!"
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

    @Test
    void login_thenGetCurrentUser_returns200() throws Exception {
        String body = """
                {
                  "email": "admin@verdora.com",
                  "password": "Password1!"
                }
                """;

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("accessToken"))
                .andReturn();

        Cookie accessToken = loginResult.getResponse()
                .getCookie("accessToken");

        assertNotNull(accessToken);

        mockMvc.perform(get("/users/current-user")
                        .cookie(accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email")
                        .value("admin@verdora.com"));
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

    // ── POST /auth/register ────────────────────────────────────────────

    @Test
    void register_allRequiredFieldsEmpty_returns400() throws Exception {
        String body = """
                {
                  "name": "",
                  "email": "",
                  "phone": "",
                  "password": ""
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(cookie().doesNotExist("accessToken"))
                .andExpect(cookie().doesNotExist("refreshToken"));
    }

    @Test
    void register_allRequiredFieldsMissing_returns400() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

}
