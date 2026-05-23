package com.verdorabackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class ProductControllerIntegrationTest extends BaseIntegrationTest {

    // ── GET /products ─────────────────────────────────────────────────────────

    @Test
    void getProducts_noFilters_returns200() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void getProducts_filterByCategory_returns200() throws Exception {
        mockMvc.perform(get("/products?categoryId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void getProducts_filterByPriceRange_returns200() throws Exception {
        mockMvc.perform(get("/products?minPrice=100&maxPrice=5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void getProducts_filterByDiscount_returns200() throws Exception {
        mockMvc.perform(get("/products?discount=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void getProducts_searchByName_returns200() throws Exception {
        mockMvc.perform(get("/products?search=ноутбук"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void getProducts_combinedFilters_returns200() throws Exception {
        mockMvc.perform(get("/products?categoryId=1&discount=true&search=laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void getProducts_pagination_returns200() throws Exception {
        mockMvc.perform(get("/products?page=0&size=5&sort=price,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pageable.pageSize").value(5));
    }

    // ── GET /products/{id} ────────────────────────────────────────────────────

    @Test
    void getProduct_existingId_returns200() throws Exception {
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productId").value(1));
    }

    @Test
    void getProduct_notFound_returns404() throws Exception {
        mockMvc.perform(get("/products/99999"))
                .andExpect(status().isNotFound());
    }

    // ── POST /products ────────────────────────────────────────────────────────

    @Test
    void createProduct_asAdmin_returns201() throws Exception {
        String body = """
                {
                  "name": "Test Product",
                  "description": "Test description for product",
                  "price": 999.99,
                  "categoryId": 1,
                  "imageUrl": "https://example.com/image.jpg",
                  "discountPrice": 899.99
                }
                """;

        mockMvc.perform(post("/products")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Test Product"))
                .andExpect(jsonPath("$.data.price").value(999.99));
    }

    @Test
    void createProduct_unauthenticated_returns401() throws Exception {
        String body = """
                {
                  "name": "Test",
                  "description": "Test description",
                  "price": 100.00,
                  "categoryId": 1,
                  "imageUrl": "https://example.com/image.jpg",
                  "discountPrice": 90.00
                }
                """;

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createProduct_categoryNotFound_returns404() throws Exception {
        String body = """
                {
                  "name": "Test",
                  "description": "Test description",
                  "price": 100.00,
                  "categoryId": 99999,
                  "imageUrl": "https://example.com/image.jpg",
                  "discountPrice": 90.00
                }
                """;

        mockMvc.perform(post("/products")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    // ── PUT /products/{id} ────────────────────────────────────────────────────

    @Test
    void updateProduct_asAdmin_returns200() throws Exception {
        String body = """
                {
                  "name": "Updated Product",
                  "description": "Updated description for product",
                  "price": 1500.00,
                  "categoryId": 1,
                  "imageUrl": "https://example.com/image.jpg",
                  "discountPrice": 1200.00
                }
                """;

        mockMvc.perform(put("/products/1")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Product"));
    }

    @Test
    void updateProduct_notFound_returns404() throws Exception {
        String body = """
                {
                  "name": "Test",
                  "description": "Test description",
                  "price": 100.00,
                  "categoryId": 1,
                  "imageUrl": "https://example.com/image.jpg",
                  "discountPrice": 90.00
                }
                """;

        mockMvc.perform(put("/products/99999")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /products/{id} ─────────────────────────────────────────────────

    @Test
    void deleteProduct_asAdmin_returns200() throws Exception {
        // Спочатку створюємо продукт щоб видалити
        String createBody = """
                {
                  "name": "To Delete",
                  "description": "Will be deleted",
                  "price": 100.00,
                  "categoryId": 1,
                  "imageUrl": "https://example.com/image.jpg",
                  "discountPrice": 90.00
                }
                """;
        String result = mockMvc.perform(post("/products")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andReturn().getResponse().getContentAsString();

        Long productId = objectMapper.readTree(result).path("data").path("productId").asLong();

        mockMvc.perform(delete("/products/" + productId)
                        .cookie(adminCookie()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProduct_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/products/99999")
                        .cookie(adminCookie()))
                .andExpect(status().isNotFound());
    }
}
