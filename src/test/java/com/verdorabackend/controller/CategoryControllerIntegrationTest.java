package com.verdorabackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void getAllCategories_returnsImageUrl() throws Exception {
        String body = """
                {
                  "name": "Home Page Category",
                  "imageUrl": "https://example.com/categories/home-page.jpg"
                }
                """;

        String result = mockMvc.perform(post("/categories")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long categoryId = objectMapper.readTree(result)
                .path("data")
                .path("categoryId")
                .asLong();

        mockMvc.perform(get("/categories")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.data.content[?(@.categoryId == "
                                + categoryId
                                + ")].imageUrl"
                ).value(hasItem(
                        "https://example.com/categories/home-page.jpg"
                )));
    }

    // ── POST /categories ──────────────────────────────────────────────────────

    @Test
    void createCategory_asAdmin_returns201WithImageUrl() throws Exception {
        String body = """
                {
                  "name": "New Category",
                  "imageUrl": "https://example.com/categories/new-category.jpg"
                }
                """;

        mockMvc.perform(post("/categories")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name")
                        .value("New Category"))
                .andExpect(jsonPath("$.data.imageUrl")
                        .value(
                                "https://example.com/categories/new-category.jpg"
                        ));
    }

    @Test
    void createCategory_duplicateNameIgnoreCase_returns409() throws Exception {
        String body = """
                {
                  "name": "ЕлектронІКА",
                  "imageUrl": "https://example.com/categories/electronics.jpg"
                }
                """;

        mockMvc.perform(post("/categories")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(
                                "Category already exists, name=ЕлектронІКА"
                        ));
    }

    @Test
    void createCategory_missingImage_returns400() throws Exception {
        String body = """
                {
                  "name": "New Category",
                  "imageUrl": ""
                }
                """;

        mockMvc.perform(post("/categories")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCategory_asUser_returns403() throws Exception {
        String body = """
                {
                  "name": "New Category",
                  "imageUrl": "https://example.com/categories/new-category.jpg"
                }
                """;

        mockMvc.perform(post("/categories")
                        .cookie(userCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void createCategory_withoutAuthentication_returns401() throws Exception {
        String body = """
                {
                  "name": "New Category",
                  "imageUrl": "https://example.com/categories/new-category.jpg"
                }
                """;

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
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
    void getCategory_returnsImageUrl() throws Exception {
        String body = """
                {
                  "name": "Category With Image",
                  "imageUrl": "https://example.com/categories/category.jpg"
                }
                """;

        String result = mockMvc.perform(post("/categories")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long categoryId = objectMapper.readTree(result)
                .path("data")
                .path("categoryId")
                .asLong();

        mockMvc.perform(get("/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.categoryId")
                        .value(categoryId))
                .andExpect(jsonPath("$.data.name")
                        .value("Category With Image"))
                .andExpect(jsonPath("$.data.imageUrl")
                        .value(
                                "https://example.com/categories/category.jpg"
                        ));
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
                  "name": "Updated",
                  "imageUrl": "https://example.com/categories/updated.jpg"
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
                  "name": "Нова категорія",
                  "imageUrl": "https://example.com/categories/updated.jpg"
                }
                """;

        mockMvc.perform(put("/categories/1")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.categoryId")
                        .value(1))
                .andExpect(jsonPath("$.data.name")
                        .value("Нова категорія"))
                .andExpect(jsonPath("$.data.imageUrl")
                        .value(
                                "https://example.com/categories/updated.jpg"
                        ));
    }

    @Test
    void updateCategory_asUser_returns403() throws Exception {
        String body = """
                {
                  "name": "Нова категорія",
                  "imageUrl": "https://example.com/categories/updated.jpg"
                }
                """;

        mockMvc.perform(put("/categories/1")
                        .cookie(userCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateCategory_withoutAuthentication_returns401() throws Exception {
        String body = """
                {
                  "name": "Нова категорія",
                  "imageUrl": "https://example.com/categories/updated.jpg"
                }
                """;

        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateCategory_duplicateNameIgnoreCase_returns409() throws Exception {
        String body = """
                {
                  "name": "одяг",
                  "imageUrl": "https://example.com/categories/clothes.jpg"
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
                  "name": "Електроніка",
                  "imageUrl": "https://example.com/categories/electronics.jpg"
                }
                """;

        mockMvc.perform(put("/categories/1")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name")
                        .value("Електроніка"))
                .andExpect(jsonPath("$.data.imageUrl")
                        .value(
                                "https://example.com/categories/electronics.jpg"
                        ));
    }

    @Test
    void updateCategory_blankName_returns400() throws Exception {
        String body = """
                {
                  "name": "   ",
                  "imageUrl": "https://example.com/categories/category.jpg"
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
                  "name": "%s",
                  "imageUrl": "https://example.com/categories/category.jpg"
                }
                """.formatted("a".repeat(257));

        mockMvc.perform(put("/categories/1")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCategory_missingImage_returns400() throws Exception {
        String body = """
                {
                  "name": "Updated Category",
                  "imageUrl": ""
                }
                """;

        mockMvc.perform(put("/categories/1")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ── DELETE /categories/{id} ───────────────────────────────────────────────

    @Test
    void deleteCategory_asAdmin_returns200() throws Exception {
        String createBody = """
                {
                  "name": "To Delete",
                  "imageUrl": "https://example.com/categories/to-delete.jpg"
                }
                """;

        String result = mockMvc.perform(post("/categories")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long categoryId = objectMapper.readTree(result)
                .path("data")
                .path("categoryId")
                .asLong();

        mockMvc.perform(delete("/categories/{id}", categoryId)
                        .cookie(adminCookie()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCategory_asUser_returns403() throws Exception {
        mockMvc.perform(delete("/categories/1")
                        .cookie(userCookie()))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteCategory_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteCategory_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/categories/99999")
                        .cookie(adminCookie()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCategory_inUse_returns409() throws Exception {
        mockMvc.perform(delete("/categories/1")
                        .cookie(adminCookie()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString(
                                "Category cannot be deleted"
                        )));
    }
}
