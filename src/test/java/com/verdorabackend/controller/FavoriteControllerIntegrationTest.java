package com.verdorabackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class FavoriteControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ── GET /favorites ────────────────────────────────────────────────────────

    @Test
    void getFavorites_authenticated_returns200() throws Exception {
        mockMvc.perform(get("/favorites")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.number").value(0))
                .andExpect(jsonPath("$.data.size").value(12))
                .andExpect(jsonPath("$.data.last").isBoolean());
    }

    @Test
    void getFavorites_empty_returnsEmptyPage() throws Exception {
        mockMvc.perform(get("/favorites")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.data.totalElements").value(0))
                .andExpect(jsonPath("$.data.last").value(true));
    }

    @Test
    void getFavorites_multiplePages_returnsPagingMetadata() throws Exception {
        mockMvc.perform(post("/favorites/1").cookie(userCookie()))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/favorites/2").cookie(userCookie()))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/favorites")
                        .cookie(userCookie())
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(2))
                .andExpect(jsonPath("$.data.last").value(false));

        mockMvc.perform(get("/favorites")
                        .cookie(userCookie())
                        .param("page", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.last").value(true));
    }

    @Test
    void getFavorites_discountedProduct_containsRegularAndDiscountPrice() throws Exception {
        jdbcTemplate.update(
                "UPDATE products SET price = 1000.00, discount_price = 800.00 WHERE product_id = 1"
        );
        mockMvc.perform(post("/favorites/1").cookie(userCookie()))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/favorites").cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].productName").exists())
                .andExpect(jsonPath("$.data.content[0].imageUrl").exists())
                .andExpect(jsonPath("$.data.content[0].price").value(1000.00))
                .andExpect(jsonPath("$.data.content[0].discountPrice").value(800.00));
    }

    @Test
    void getFavorites_pageSizeAboveLimit_isCapped() throws Exception {
        mockMvc.perform(get("/favorites")
                        .cookie(userCookie())
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size").value(12));
    }

    @Test
    void getFavorites_invalidPagination_usesDefaults() throws Exception {
        mockMvc.perform(get("/favorites")
                        .cookie(userCookie())
                        .param("page", "-1")
                        .param("size", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.number").value(0))
                .andExpect(jsonPath("$.data.size").value(12));
    }

    @Test
    void getFavorites_cannotSeeOtherUsersFavorites() throws Exception {
        mockMvc.perform(post("/favorites/1").cookie(userCookie()))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/favorites")
                        .cookie(adminCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }

    @Test
    void addToCart_doesNotRemoveFromFavorites() throws Exception {
        mockMvc.perform(post("/favorites/1").cookie(userCookie()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/cart/items")
                        .cookie(userCookie())
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"productId\":1,\"quantity\":1}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/favorites")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].productId").value(1));
    }

    @Test
    void getFavorites_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/favorites"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getFavorites_withPagination_returnsPage() throws Exception {
        mockMvc.perform(get("/favorites")
                        .cookie(userCookie())
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.number").value(0))
                .andExpect(jsonPath("$.data.size").value(5))
                .andExpect(jsonPath("$.data.totalElements").isNumber());
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
                .andExpect(jsonPath("$.data.productId").value(1))
                .andExpect(jsonPath("$.data.favorite").value(true));
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

    @Test
    void addFavorite_afterAdding_appearsInFavoritesList() throws Exception {

        mockMvc.perform(post("/favorites/1")
                        .cookie(userCookie()))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/favorites")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[*].productId")
                        .value(org.hamcrest.Matchers.hasItem(1)));
    }

    @Test
    void addFavorite_invalidProductId_returns400() throws Exception {

        mockMvc.perform(post("/favorites/abc")
                        .cookie(userCookie()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFavorite_invalidToken_returns401() throws Exception {

        mockMvc.perform(post("/favorites/1")
                        .cookie(
                                new jakarta.servlet.http.Cookie(
                                        "accessToken",
                                        "invalid-token"
                                )
                        ))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    // ── DELETE /favorites/{productId} ─────────────────────────────────────────

    @Test
    void removeFavorite_exists_returns200AndUpdatesState() throws Exception {

        mockMvc.perform(post("/favorites/1")
                        .cookie(userCookie()))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/favorites/1")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productId").value(1))
                .andExpect(jsonPath("$.data.favorite").value(false));

        mockMvc.perform(get("/favorites")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[*].productId")
                        .value(org.hamcrest.Matchers.not(
                                org.hamcrest.Matchers.hasItem(1)
                        )));

        mockMvc.perform(get("/favorites/1")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false));
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

    @Test
    void removeFavorite_anotherUsersFavorite_returns404() throws Exception {

        mockMvc.perform(post("/favorites/1")
                        .cookie(userCookie()))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/favorites/1")
                        .cookie(adminCookie()))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/favorites/1")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void removeFavorite_invalidProductId_returns400() throws Exception {

        mockMvc.perform(delete("/favorites/abc")
                        .cookie(userCookie()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeFavorite_invalidToken_returns401() throws Exception {

        mockMvc.perform(delete("/favorites/1")
                        .cookie(new jakarta.servlet.http.Cookie(
                                "accessToken",
                                "invalid-token"
                        )))
                .andExpect(status().isUnauthorized());
    }
}
