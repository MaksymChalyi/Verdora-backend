package com.verdorabackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class AdminProductControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void getAllProducts_asAdmin_returns200() throws Exception {
        mockMvc.perform(get("/admin/products")
                        .cookie(adminCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.pageable.pageSize").value(12));
    }

    @Test
    void getAllProducts_asUser_returns403() throws Exception {
        mockMvc.perform(get("/admin/products")
                        .cookie(userCookie()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Forbidden"));
    }

    @Test
    void getAllProducts_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/admin/products"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @Test
    void getAllProducts_pageSizeCannotExceed12() throws Exception {
        mockMvc.perform(get("/admin/products?page=0&size=50")
                        .cookie(adminCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pageable.pageSize").value(12));
    }

    @Test
    void getAllProducts_returnsProductFields() throws Exception {
        mockMvc.perform(get("/admin/products")
                        .cookie(adminCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].productId").exists())
                .andExpect(jsonPath("$.data.content[0].name").exists())
                .andExpect(jsonPath("$.data.content[0].categoryId").exists())
                .andExpect(jsonPath("$.data.content[0].price").exists())
                .andExpect(jsonPath("$.data.content[0].discountPrice").exists());
    }
}
