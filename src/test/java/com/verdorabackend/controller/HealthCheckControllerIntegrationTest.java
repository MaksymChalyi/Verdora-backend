package com.verdorabackend.controller;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HealthCheckControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void ping_returns200() throws Exception {
        mockMvc.perform(get("/health/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("pong"));
    }
}
