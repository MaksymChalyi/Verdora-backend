package com.verdorabackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class FavoriteControllerIntegrationTest extends BaseIntegrationTest {

    // ── GET /favorites ────────────────────────────────────────────────────────

    @Test
    void getFavorites_authenticated_returns200() throws Exception {
        mockMvc.perform(get("/favorites")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getFavorites_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/favorites"))
                .andExpect(status().isUnauthorized());
    }

    // ── GET /favorites/{productId} ────────────────────────────────────────────

    @Test
    void isFavorite_notInFavorites_returnsFalse() throws Exception {
        mockMvc.perform(get("/favorites/1")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    void isFavorite_afterAdding_returnsTrue() throws Exception {
        // Додаємо в обране
        mockMvc.perform(post("/favorites/1").cookie(userCookie()));

        mockMvc.perform(get("/favorites/1")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

    // ── POST /favorites/{productId} ───────────────────────────────────────────

    @Test
    void addFavorite_validProduct_returns201() throws Exception {
        mockMvc.perform(post("/favorites/1")
                        .cookie(userCookie()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.productId").value(1));
    }

    @Test
    void addFavorite_alreadyExists_returns409() throws Exception {
        mockMvc.perform(post("/favorites/1").cookie(userCookie()));

        mockMvc.perform(post("/favorites/1")
                        .cookie(userCookie()))
                .andExpect(status().isConflict());
    }

    @Test
    void addFavorite_productNotFound_returns404() throws Exception {
        mockMvc.perform(post("/favorites/99999")
                        .cookie(userCookie()))
                .andExpect(status().isNotFound());
    }

    @Test
    void addFavorite_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/favorites/1"))
                .andExpect(status().isUnauthorized());
    }

    // ── DELETE /favorites/{productId} ─────────────────────────────────────────

    @Test
    void removeFavorite_exists_returns200() throws Exception {
        mockMvc.perform(post("/favorites/1").cookie(userCookie()));

        mockMvc.perform(delete("/favorites/1")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Removed from favorites"));
    }

    @Test
    void removeFavorite_notExists_returns404() throws Exception {
        mockMvc.perform(delete("/favorites/99999")
                        .cookie(userCookie()))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeFavorite_unauthenticated_returns401() throws Exception {
        mockMvc.perform(delete("/favorites/1"))
                .andExpect(status().isUnauthorized());
    }
}
