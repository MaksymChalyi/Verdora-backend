package com.verdorabackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class CartControllerIntegrationTest extends BaseIntegrationTest {

    // ── GET /cart ─────────────────────────────────────────────────────────────

    @Test
    void getCart_authenticated_returns200() throws Exception {
        mockMvc.perform(get("/cart")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.cartId").exists())
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.totalPrice").exists());
    }

    @Test
    void getCart_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /cart/items ──────────────────────────────────────────────────────

    @Test
    void addItem_validProduct_returns200() throws Exception {
        String body = """
                {
                  "productId": 1,
                  "quantity": 2
                }
                """;

        mockMvc.perform(post("/cart/items")
                        .cookie(userCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items").isArray());
    }

    @Test
    void addItem_productNotFound_returns404() throws Exception {
        String body = """
                {
                  "productId": 99999,
                  "quantity": 1
                }
                """;

        mockMvc.perform(post("/cart/items")
                        .cookie(userCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void addItem_unauthenticated_returns401() throws Exception {
        String body = """
                {
                  "productId": 1,
                  "quantity": 1
                }
                """;

        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    // ── PUT /cart/items/{id} ──────────────────────────────────────────────────
    @Test
    void updateItem_notFound_returns404() throws Exception {
        String body = """
                { "quantity": 5 }
                """;

        mockMvc.perform(put("/cart/items/99999")
                        .cookie(userCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /cart/items/{id} ───────────────────────────────────────────────

    @Test
    void removeItem_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/cart/items/99999")
                        .cookie(userCookie()))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /cart ──────────────────────────────────────────────────────────

    @Test
    void clearCart_authenticated_returns200() throws Exception {
        mockMvc.perform(delete("/cart")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cart cleared successfully"));
    }

    @Test
    void clearCart_unauthenticated_returns401() throws Exception {
        mockMvc.perform(delete("/cart"))
                .andExpect(status().isUnauthorized());
    }
}
