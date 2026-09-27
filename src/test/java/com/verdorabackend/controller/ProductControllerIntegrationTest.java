package com.verdorabackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void getProducts_sortByPriceDescending_returns200() throws Exception {
        mockMvc.perform(get("/products?sort=price,desc"))
                .andExpect(status().isOk());
    }

    @Test
    void getProducts_sortByDateAdded_returns200() throws Exception {
        mockMvc.perform(get("/products?sort=createdAt,desc"))
                .andExpect(status().isOk());
    }

    @Test
    void getProducts_sortByNameAscending_returns200() throws Exception {
        mockMvc.perform(get("/products?sort=name,asc"))
                .andExpect(status().isOk());
    }

    @Test
    void getProducts_sortByNameDescending_returns200() throws Exception {
        mockMvc.perform(get("/products?sort=name,desc"))
                .andExpect(status().isOk());
    }

    @Test
    void getProducts_noResults_returns200WithEmptyList() throws Exception {
        mockMvc.perform(get("/products?search=THIS_PRODUCT_DOES_NOT_EXIST_123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }

    @Test
    void getProducts_searchIsCaseInsensitive_returnsResults() throws Exception {
        mockMvc.perform(get("/products?search=НОУТБУК"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isNotEmpty());
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

    @Test
    void createProduct_asUser_returns403() throws Exception {
        String body = """
            {
              "name": "Test Product",
              "description": "Description",
              "price": 1000,
              "categoryId": 1,
              "imageUrl": "https://example.com/image.jpg",
              "discountPrice": 900
            }
            """;

        mockMvc.perform(post("/products")
                        .cookie(userCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void createProduct_missingRequiredFields_returns400() throws Exception {
        String body = """
            {
              "name": "",
              "price": null,
              "categoryId": null,
              "imageUrl": ""
            }
            """;

        mockMvc.perform(post("/products")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_discountPriceNotLowerThanPrice_returns400() throws Exception {
        String body = """
            {
              "name": "Test Product",
              "price": 1000,
              "categoryId": 1,
              "imageUrl": "https://example.com/image.jpg",
              "discountPrice": 1000
            }
            """;

        mockMvc.perform(post("/products")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString(
                                "Discount price must be lower than regular price"
                        )));
    }

    @Test
    void createProduct_withoutOptionalFields_returns201() throws Exception {
        String body = """
            {
              "name": "Product Without Discount",
              "price": 1000,
              "categoryId": 1,
              "imageUrl": "https://example.com/image.jpg"
            }
            """;

        mockMvc.perform(post("/products")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name")
                        .value("Product Without Discount"));
    }

    // ── PUT /products/{id} ─────────────────────────────────────────────────────

    @Test
    void updateProduct_asAdmin_returns200WithUpdatedProduct() throws Exception {
        String body = """
                {
                  "name": "Updated Product",
                  "description": "Updated description",
                  "price": 1500.00,
                  "categoryId": 1,
                  "imageUrl": "https://example.com/updated-image.jpg",
                  "discountPrice": 1200.00
                }
                """;

        mockMvc.perform(put("/products/1")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productId").value(1))
                .andExpect(jsonPath("$.data.name").value("Updated Product"))
                .andExpect(jsonPath("$.data.description").value("Updated description"))
                .andExpect(jsonPath("$.data.price").value(1500.00))
                .andExpect(jsonPath("$.data.categoryId").value(1))
                .andExpect(jsonPath("$.data.imageUrl")
                        .value("https://example.com/updated-image.jpg"))
                .andExpect(jsonPath("$.data.discountPrice").value(1200.00));
    }

    @Test
    void updateProduct_notFound_returns404() throws Exception {
        String body = """
                {
                  "name": "Updated Product",
                  "description": "Description",
                  "price": 1000.00,
                  "categoryId": 1,
                  "imageUrl": "https://example.com/image.jpg",
                  "discountPrice": 900.00
                }
                """;

        mockMvc.perform(put("/products/99999")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProduct_asUser_returns403() throws Exception {
        String body = """
            {
              "name": "Updated Product",
              "description": "Description",
              "price": 1000.00,
              "categoryId": 1,
              "imageUrl": "https://example.com/image.jpg",
              "discountPrice": 900.00
            }
            """;

        mockMvc.perform(put("/products/1")
                        .cookie(userCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateProduct_withoutAuthentication_returns401() throws Exception {
        String body = """
            {
              "name": "Updated Product",
              "description": "Description",
              "price": 1000.00,
              "categoryId": 1,
              "imageUrl": "https://example.com/image.jpg",
              "discountPrice": 900.00
            }
            """;

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateProduct_missingName_returns400() throws Exception {
        String body = """
            {
              "name": "",
              "price": 1000,
              "categoryId": 1,
              "imageUrl": "https://example.com/image.jpg"
            }
            """;

        mockMvc.perform(put("/products/1")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Name is required"));
    }

    @Test
    void updateProduct_missingPrice_returns400() throws Exception {
        String body = """
            {
              "name": "Product",
              "price": null,
              "categoryId": 1,
              "imageUrl": "https://example.com/image.jpg"
            }
            """;

        mockMvc.perform(put("/products/1")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Price is required"));
    }

    @Test
    void updateProduct_missingCategory_returns400() throws Exception {
        String body = """
            {
              "name": "Product",
              "price": 1000,
              "categoryId": null,
              "imageUrl": "https://example.com/image.jpg"
            }
            """;

        mockMvc.perform(put("/products/1")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Category is required"));
    }

    @Test
    void updateProduct_missingImage_returns400() throws Exception {
        String body = """
            {
              "name": "Product",
              "price": 1000,
              "categoryId": 1,
              "imageUrl": ""
            }
            """;

        mockMvc.perform(put("/products/1")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Image is required"));
    }

    @Test
    void updateProduct_discountPriceNotLowerThanPrice_returns400() throws Exception {
        String body = """
            {
              "name": "Updated Product",
              "description": "Description",
              "price": 1000.00,
              "categoryId": 1,
              "imageUrl": "https://example.com/image.jpg",
              "discountPrice": 1000.00
            }
            """;

        mockMvc.perform(put("/products/1")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString(
                                "Discount price must be lower than regular price"
                        )));
    }

    @Test
    void updateProduct_categoryNotFound_returns404() throws Exception {
        String body = """
            {
              "name": "Updated Product",
              "description": "Description",
              "price": 1000.00,
              "categoryId": 99999,
              "imageUrl": "https://example.com/image.jpg",
              "discountPrice": 900.00
            }
            """;

        mockMvc.perform(put("/products/1")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProduct_multipleCategories_returns400() throws Exception {
        String body = """
            {
              "name": "Updated Product",
              "description": "Description",
              "price": 1000.00,
              "categoryId": [1, 2],
              "imageUrl": "https://example.com/image.jpg",
              "discountPrice": 900.00
            }
            """;

        mockMvc.perform(put("/products/1")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProduct_withoutOptionalFields_returns200() throws Exception {
        String body = """
            {
              "name": "Updated Without Optional Fields",
              "price": 1000.00,
              "categoryId": 1,
              "imageUrl": "https://example.com/image.jpg"
            }
            """;

        mockMvc.perform(put("/products/1")
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name")
                        .value("Updated Without Optional Fields"))
                .andExpect(jsonPath("$.data.price").value(1000.00))
                .andExpect(jsonPath("$.data.categoryId").value(1))
                .andExpect(jsonPath("$.data.imageUrl")
                        .value("https://example.com/image.jpg"))
                .andExpect(jsonPath("$.data.description").doesNotExist())
                .andExpect(jsonPath("$.data.discountPrice").doesNotExist());
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
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long productId = objectMapper.readTree(result)
                .path("data")
                .path("productId")
                .asLong();

        mockMvc.perform(delete("/products/" + productId)
                        .cookie(adminCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Product deleted successfully"));

        mockMvc.perform(get("/products/" + productId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/products/99999")
                        .cookie(adminCookie()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct_asUser_returns403() throws Exception {
        mockMvc.perform(delete("/products/1")
                        .cookie(userCookie()))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteProduct_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isUnauthorized());
    }

}
