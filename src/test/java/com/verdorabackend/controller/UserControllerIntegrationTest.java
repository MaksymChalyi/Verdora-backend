package com.verdorabackend.controller;

import com.verdorabackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    // ── GET /users/current-user ───────────────────────────────────────────────

    @Test
    void getCurrentUser_authenticated_returns200() throws Exception {
        mockMvc.perform(get("/users/current-user")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").exists())
                .andExpect(jsonPath("$.data.email").exists())
                .andExpect(jsonPath("$.data.id").doesNotExist())
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.data.role").doesNotExist());
    }

    @Test
    void getCurrentUser_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/users/current-user"))
                .andExpect(status().isUnauthorized());
    }

    // ── PUT /users/current-user ───────────────────────────────────────────────

    @Test
    void updateCurrentUser_validData_returns200() throws Exception {
        String body = """
                {
                  "name": "Updated User",
                  "phone": "+380501234567"
                }
                """;

        mockMvc.perform(put("/users/current-user")
                        .cookie(userCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Profile updated successfully"))
                .andExpect(jsonPath("$.data.name")
                        .value("Updated User"))
                .andExpect(jsonPath("$.data.phone")
                        .value("+380501234567"))
                .andExpect(jsonPath("$.data.email").exists())
                .andExpect(jsonPath("$.data.id").doesNotExist());
    }

    @Test
    void updateCurrentUser_withoutEmail_returns200() throws Exception {
        String body = """
                {
                  "name": "Updated User",
                  "phone": "+380501234567"
                }
                """;

        mockMvc.perform(put("/users/current-user")
                        .cookie(userCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").exists());
    }

    @Test
    void updateCurrentUser_invalidPhone_returns400() throws Exception {
        String body = """
                {
                  "name": "Updated User",
                  "phone": "12345"
                }
                """;

        mockMvc.perform(put("/users/current-user")
                        .cookie(userCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCurrentUser_blankName_returns400() throws Exception {
        String body = """
                {
                  "name": "   ",
                  "phone": "+380501234567"
                }
                """;

        mockMvc.perform(put("/users/current-user")
                        .cookie(userCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCurrentUser_unauthenticated_returns401() throws Exception {
        String body = """
                {
                  "name": "Updated User",
                  "phone": "+380501234567"
                }
                """;

        mockMvc.perform(put("/users/current-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateCurrentUser_afterUpdate_getProfileReturnsUpdatedData() throws Exception {
        String body = """
                {
                  "name": "Updated User",
                  "phone": "+380501234567"
                }
                """;

        mockMvc.perform(put("/users/current-user")
                        .cookie(userCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/current-user")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name")
                        .value("Updated User"))
                .andExpect(jsonPath("$.data.phone")
                        .value("+380501234567"))
                .andExpect(jsonPath("$.data.email").exists());
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

    // ── DELETE /users/current-user ────────────────────────────────────────────

    @Test
    void deleteCurrentUser_unauthenticated_returns401() throws Exception {
        String body = """
                {
                  "password": "Password1!"
                }
                """;

        mockMvc.perform(delete("/users/current-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteCurrentUser_missingPassword_returns400() throws Exception {
        String body = """
                {
                  "password": ""
                }
                """;

        mockMvc.perform(delete("/users/current-user")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCurrentUser_invalidPassword_returns401() throws Exception {
        String body = """
                {
                  "password": "WrongPassword123!"
                }
                """;

        mockMvc.perform(delete("/users/current-user")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid password"));

        assert userRepository.findUserByEmail("admin@verdora.com").isPresent();
    }

    @Test
    void deleteCurrentUser_validPassword_returns200AndDeletesAccount() throws Exception {
        String body = """
                {
                  "password": "Password1!"
                }
                """;

        mockMvc.perform(delete("/users/current-user")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Account deleted successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        assert userRepository.findUserByEmail("admin@verdora.com").isEmpty();
    }

    @Test
    void deleteCurrentUser_afterDeletion_oldAccessTokenReturns401() throws Exception {
        var accessTokenCookie = adminCookie();

        String body = """
                {
                  "password": "Password1!"
                }
                """;

        mockMvc.perform(delete("/users/current-user")
                        .cookie(accessTokenCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/current-user")
                        .cookie(accessTokenCookie))
                .andExpect(status().isUnauthorized());
    }
}
