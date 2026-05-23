package com.verdorabackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class CategoryControllerIntegrationTest extends BaseIntegrationTest {

    // ── GET /categories ───────────────────────────────────────────────────────

    @Test
    void getAllCategories_returns200() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    // ── GET /categories/{id} ──────────────────────────────────────────────────

    @Test
    void getCategory_existingId_returns200() throws Exception {
        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.categoryId").value(1));
    }

    @Test
    void getCategory_notFound_returns404() throws Exception {
        mockMvc.perform(get("/categories/99999"))
                .andExpect(status().isNotFound());
    }

    // ── PUT /categories/{id} ──────────────────────────────────────────────────

    @Test
    void updateCategory_notFound_returns404() throws Exception {
        String body = """
                {
                  "name": "Updated"
                }
                """;

        mockMvc.perform(put("/categories/99999")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /categories/{id} ───────────────────────────────────────────────

    @Test
    void deleteCategory_asAdmin_returns200() throws Exception {
        // Створюємо нову категорію без продуктів щоб безпечно видалити
        String createBody = """
                { "name": "To Delete" }
                """;
        String result = mockMvc.perform(post("/categories")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andReturn().getResponse().getContentAsString();

        Long categoryId = objectMapper.readTree(result).path("data").path("categoryId").asLong();

        mockMvc.perform(delete("/categories/" + categoryId)
                        .cookie(adminCookie()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCategory_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/categories/99999")
                        .cookie(adminCookie()))
                .andExpect(status().isNotFound());
    }
}
