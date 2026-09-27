package com.verdorabackend.controller;

import com.verdorabackend.entity.Order;
import com.verdorabackend.entity.OrderStatus;
import com.verdorabackend.entity.User;
import com.verdorabackend.repository.OrderRepository;
import com.verdorabackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class OrderControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    // ── POST /orders ──────────────────────────────────────────────────────────

    @Test
    void placeOrder_emptyCart_returns400() throws Exception {
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

    // ── GET /orders/all ───────────────────────────────────────────────────────

    @Test
    void getAllOrders_asAdmin_returns200() throws Exception {
        mockMvc.perform(get("/orders/all")
                        .cookie(adminCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.size").value(12));
    }

    @Test
    void getAllOrders_asUser_returns403() throws Exception {
        mockMvc.perform(get("/orders/all")
                        .cookie(userCookie()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllOrders_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(get("/orders/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllOrders_filterByStatus_returnsMatchingOrders() throws Exception {
        createOrder(OrderStatus.SHIPPED, BigDecimal.valueOf(100));
        createOrder(OrderStatus.DELIVERED, BigDecimal.valueOf(200));

        mockMvc.perform(get("/orders/all")
                        .cookie(adminCookie())
                        .param("status", "DELIVERED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].status").value("DELIVERED"));
    }

    @Test
    void getAllOrders_filterByDate_returnsMatchingOrders() throws Exception {
        createOrder(OrderStatus.PENDING, BigDecimal.valueOf(100));
        String today = LocalDate.now().toString();

        mockMvc.perform(get("/orders/all")
                        .cookie(adminCookie())
                        .param("dateFrom", today)
                        .param("dateTo", today))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    @Test
    void getAllOrders_sortByTotalPriceAscending_returnsSortedOrders() throws Exception {
        createOrder(OrderStatus.PENDING, BigDecimal.valueOf(200));
        createOrder(OrderStatus.PAID, BigDecimal.valueOf(100));

        mockMvc.perform(get("/orders/all")
                        .cookie(adminCookie())
                        .param("sort", "totalPrice,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].totalPrice").value(100));
    }

    // ── GET /orders/{id} ──────────────────────────────────────────────────────

    @Test
    void getOrder_notFound_returns404() throws Exception {
        mockMvc.perform(get("/orders/99999")
                        .cookie(userCookie()))
                .andExpect(status().isNotFound());
    }

    // ── PATCH /orders/{id}/status ─────────────────────────────────────────────

    @Test
    void updateOrderStatus_shippedToDelivered_returns200() throws Exception {
        Order order = createOrder(OrderStatus.SHIPPED, BigDecimal.valueOf(100));

        mockMvc.perform(patch("/orders/{orderId}/status", order.getId())
                        .cookie(adminCookie())
                        .contentType("application/json")
                        .content("{\"status\":\"DELIVERED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DELIVERED"));
    }

    @Test
    void updateOrderStatus_asUser_returns403() throws Exception {
        Order order = createOrder(OrderStatus.SHIPPED, BigDecimal.valueOf(100));

        mockMvc.perform(patch("/orders/{orderId}/status", order.getId())
                        .cookie(userCookie())
                        .contentType("application/json")
                        .content("{\"status\":\"DELIVERED\"}"))
                .andExpect(status().isForbidden());
    }

    // ── DELETE /orders/{id} ───────────────────────────────────────────────────

    @Test
    void cancelOrder_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/orders/99999")
                        .cookie(userCookie()))
                .andExpect(status().isNotFound());
    }

    private Order createOrder(OrderStatus status, BigDecimal totalPrice) {
        User user = userRepository.findById(2L).orElseThrow();

        Order order = new Order();
        order.setUser(user);
        order.setStatus(status);
        order.setTotalPrice(totalPrice);

        return orderRepository.saveAndFlush(order);
    }
}
