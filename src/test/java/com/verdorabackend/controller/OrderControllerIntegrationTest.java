package com.verdorabackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class OrderControllerIntegrationTest extends BaseIntegrationTest {

    // ── POST /orders ──────────────────────────────────────────────────────────

    @Test
    void placeOrder_emptyCart_returns400() throws Exception {
        // Очищаємо кошик перед тестом
        mockMvc.perform(delete("/cart").cookie(userCookie()));

        mockMvc.perform(post("/orders")
                        .cookie(userCookie()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void placeOrder_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/orders"))
                .andExpect(status().isUnauthorized());
    }

    // ── GET /orders ───────────────────────────────────────────────────────────

    @Test
    void getOrders_authenticated_returns200() throws Exception {
        mockMvc.perform(get("/orders")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getOrders_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isUnauthorized());
    }

    // ── GET /orders/{id} ──────────────────────────────────────────────────────

    @Test
    void getOrder_notFound_returns404() throws Exception {
        mockMvc.perform(get("/orders/99999")
                        .cookie(userCookie()))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /orders/{id} ───────────────────────────────────────────────────

    @Test
    void cancelOrder_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/orders/99999")
                        .cookie(userCookie()))
                .andExpect(status().isNotFound());
    }

}
