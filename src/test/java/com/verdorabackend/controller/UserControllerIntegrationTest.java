package com.verdorabackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class UserControllerIntegrationTest extends BaseIntegrationTest {

    // ── GET /users/current-user ───────────────────────────────────────────────

    @Test
    void getCurrentUser_authenticated_returns200() throws Exception {
        mockMvc.perform(get("/users/current-user")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").exists());
    }

    @Test
    void getCurrentUser_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/users/current-user"))
                .andExpect(status().isUnauthorized());
    }

    // ── GET /users (ADMIN) ────────────────────────────────────────────────────

    @Test
    void getAllUsers_asAdmin_returns200() throws Exception {
        mockMvc.perform(get("/users")
                        .cookie(adminCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void getAllUsers_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());
    }

    // ── PUT /users/{id} ───────────────────────────────────────────────────────

    @Test
    void updateUser_unauthenticated_returns401() throws Exception {
        String body = """
                {
                  "name": "Test",
                  "phoneNumber": "+380501234567"
                }
                """;

        mockMvc.perform(put("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    // ── DELETE /users/{id} ────────────────────────────────────────────────────

    @Test
    void deleteUser_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/users/99999")
                        .cookie(userCookie()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_unauthenticated_returns401() throws Exception {
        mockMvc.perform(delete("/users/2"))
                .andExpect(status().isUnauthorized());
    }
}
