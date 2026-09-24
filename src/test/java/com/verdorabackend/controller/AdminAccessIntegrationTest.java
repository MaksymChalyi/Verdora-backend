package com.verdorabackend.controller;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminAccessIntegrationTest extends BaseIntegrationTest {

    @Test
    void adminEndpoint_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(get("/admin/orders"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @Test
    void adminEndpoint_withUserRole_returns403() throws Exception {
        mockMvc.perform(get("/admin/orders")
                        .cookie(userCookie()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Forbidden"));
    }

    @Test
    void adminEndpoint_withAdminRole_returns200() throws Exception {
        mockMvc.perform(get("/admin/orders")
                        .cookie(adminCookie()))
                .andExpect(status().isOk());
    }

    @Test
    void adminCategories_withUserRole_returns403() throws Exception {
        mockMvc.perform(get("/admin/categories")
                        .cookie(userCookie()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Forbidden"));
    }
}
