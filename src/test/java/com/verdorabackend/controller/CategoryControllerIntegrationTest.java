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
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.number").value(0))
                .andExpect(jsonPath("$.data.size").value(12));
    }

    // ── GET /admin/categories ────────────────────────────────────────────────

    @Test
    void getAllCategories_asAdmin_returns200() throws Exception {
        mockMvc.perform(get("/admin/categories")
                        .cookie(adminCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].name").isNotEmpty());
    }

    @Test
    void getAllCategories_asUser_returns403() throws Exception {
        mockMvc.perform(get("/admin/categories")
                        .cookie(userCookie()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllCategories_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(get("/admin/categories"))
                .andExpect(status().isUnauthorized());
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

    @Test
    void updateCategory_asAdmin_returns200() throws Exception {
        String body = """
                {
                  "name": "Нова категорія"
                }
                """;

        mockMvc.perform(put("/categories/1")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.categoryId").value(1))
                .andExpect(jsonPath("$.data.name").value("Нова категорія"));
    }

    @Test
    void updateCategory_asUser_returns403() throws Exception {
        String body = """
                {
                  "name": "Нова категорія"
                }
                """;

        mockMvc.perform(put("/categories/1")
                        .cookie(userCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateCategory_duplicateNameIgnoreCase_returns409() throws Exception {
        String body = """
                {
                  "name": "одяг"
                }
                """;

        mockMvc.perform(put("/categories/1")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void updateCategory_sameName_returns200() throws Exception {
        String body = """
                {
                  "name": "Електроніка"
                }
                """;

        mockMvc.perform(put("/categories/1")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Електроніка"));
    }

    @Test
    void updateCategory_blankName_returns400() throws Exception {
        String body = """
                {
                  "name": "   "
                }
                """;

        mockMvc.perform(put("/categories/1")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCategory_nameTooLong_returns400() throws Exception {
        String body = """
                {
                  "name": "%s"
                }
                """.formatted("a".repeat(257));

        mockMvc.perform(put("/categories/1")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
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
