package com.verdorabackend.controller;

import com.verdorabackend.entity.Order;
import com.verdorabackend.entity.OrderStatus;
import com.verdorabackend.entity.User;
import com.verdorabackend.repository.OrderRepository;
import com.verdorabackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
    void placeOrder_discountedProduct_usesDiscountPrice() throws Exception {
        String body = """
                {
                  "productId": 1,
                  "quantity": 2
                }
                """;

        mockMvc.perform(post("/cart/items")
                        .cookie(userCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(post("/orders")
                        .cookie(userCookie()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.totalPrice").value(1600.00))
                .andExpect(jsonPath("$.data.items[0].priceAtPurchase").value(800.00))
                .andExpect(jsonPath("$.data.items[0].subtotal").value(1600.00));
    }

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
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.size").value(12))
                .andExpect(jsonPath("$.data.number").value(0));
    }

    @Test
    void getOrders_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getOrders_pageSizeIsLimitedTo12() throws Exception {
        for (int i = 0; i < 13; i++) {
            createOrder(OrderStatus.PENDING, BigDecimal.valueOf(100 + i));
        }

        mockMvc.perform(get("/orders")
                        .cookie(userCookie())
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size").value(12))
                .andExpect(jsonPath("$.data.content.length()").value(12));
    }

    @Test
    void getOrders_secondPage_returns200() throws Exception {
        for (int i = 0; i < 13; i++) {
            createOrder(
                    OrderStatus.PENDING,
                    BigDecimal.valueOf(100 + i)
            );
        }

        mockMvc.perform(get("/orders")
                        .cookie(userCookie())
                        .param("page", "1")
                        .param("size", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.number").value(1))
                .andExpect(jsonPath("$.data.content").isArray());
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

    @Test
    void getOrder_existingOrder_returnsFullDetails() throws Exception {
        String body = """
                {
                  "productId": 1,
                  "quantity": 2
                }
                """;

        mockMvc.perform(post("/cart/items")
                        .cookie(userCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        String result = mockMvc.perform(post("/orders")
                        .cookie(userCookie()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long orderId = objectMapper.readTree(result)
                .path("data")
                .path("orderId")
                .asLong();

        mockMvc.perform(get("/orders/{orderId}", orderId)
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId")
                        .value(orderId))
                .andExpect(jsonPath("$.data.createdAt")
                        .exists())
                .andExpect(jsonPath("$.data.totalPrice")
                        .exists())
                .andExpect(jsonPath("$.data.status")
                        .exists())
                .andExpect(jsonPath("$.data.items")
                        .isArray())
                .andExpect(jsonPath("$.data.items[0].productId")
                        .value(1))
                .andExpect(jsonPath("$.data.items[0].productName")
                        .exists())
                .andExpect(jsonPath("$.data.items[0].imageUrl")
                        .exists())
                .andExpect(jsonPath("$.data.items[0].categoryName")
                        .exists())
                .andExpect(jsonPath("$.data.items[0].quantity")
                        .value(2))
                .andExpect(jsonPath("$.data.items[0].priceAtPurchase")
                        .exists())
                .andExpect(jsonPath("$.data.items[0].subtotal")
                        .exists());
    }

    @Test
    void getOrder_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void orderStatus_isConsistentAcrossHistoryDetailsAndTracking() throws Exception {
        Order order = createOrder(OrderStatus.PAID, BigDecimal.valueOf(200));

        mockMvc.perform(get("/orders")
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].orderId").value(order.getId()))
                .andExpect(jsonPath("$.data.content[0].status").value("PAID"));

        mockMvc.perform(get("/orders/{orderId}", order.getId())
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"));

        mockMvc.perform(get("/orders/{orderId}/status", order.getId())
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"));
    }

    @Test
    void getOrder_anotherUsersOrder_returns404() throws Exception {
        Order order = createOrder(OrderStatus.PAID, BigDecimal.valueOf(200));

        mockMvc.perform(get("/orders/{orderId}", order.getId())
                        .cookie(adminCookie()))
                .andExpect(status().isNotFound());
    }

    // ── GET /orders/{id}/status ──────────────────────────────────────────────────

    @Test
    void getOrderStatus_activeOrder_returns200AndFinalStatusFalse()
            throws Exception {

        Order order = createOrder(
                OrderStatus.SHIPPED,
                BigDecimal.valueOf(100)
        );

        mockMvc.perform(get("/orders/{orderId}/status", order.getId())
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId")
                        .value(order.getId()))
                .andExpect(jsonPath("$.data.status")
                        .value("SHIPPED"))
                .andExpect(jsonPath("$.data.finalStatus")
                        .value(false));
    }

    @Test
    void getOrderStatus_delivered_returnsFinalStatusTrue()
            throws Exception {

        Order order = createOrder(
                OrderStatus.DELIVERED,
                BigDecimal.valueOf(100)
        );

        mockMvc.perform(get("/orders/{orderId}/status", order.getId())
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status")
                        .value("DELIVERED"))
                .andExpect(jsonPath("$.data.finalStatus")
                        .value(true));
    }

    @Test
    void getOrderStatus_cancelled_returnsFinalStatusTrue()
            throws Exception {

        Order order = createOrder(
                OrderStatus.CANCELLED,
                BigDecimal.valueOf(100)
        );

        mockMvc.perform(get("/orders/{orderId}/status", order.getId())
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status")
                        .value("CANCELLED"))
                .andExpect(jsonPath("$.data.finalStatus")
                        .value(true));
    }

    @Test
    void getOrderStatus_notFound_returns404() throws Exception {
        mockMvc.perform(get("/orders/99999/status")
                        .cookie(userCookie()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOrderStatus_anotherUsersOrder_returns404()
            throws Exception {

        Order order = createOrder(
                OrderStatus.PAID,
                BigDecimal.valueOf(100)
        );

        mockMvc.perform(get("/orders/{orderId}/status", order.getId())
                        .cookie(adminCookie()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOrderStatus_withoutAuthentication_returns401()
            throws Exception {

        mockMvc.perform(get("/orders/1/status"))
                .andExpect(status().isUnauthorized());
    }

    // ── GET /orders/{id}/details ──────────────────────────────────────────────

    @Test
    void getOrderDetails_asAdmin_returns200() throws Exception {
        Order order = createOrder(OrderStatus.PAID, BigDecimal.valueOf(200));

        mockMvc.perform(get("/orders/{orderId}/details", order.getId())
                        .cookie(adminCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value(order.getId()))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.totalPrice").value(200))
                .andExpect(jsonPath("$.data.status").value("PAID"))
                .andExpect(jsonPath("$.data.customer").exists())
                .andExpect(jsonPath("$.data.customer.id").value(2))
                .andExpect(jsonPath("$.data.customer.name").exists())
                .andExpect(jsonPath("$.data.customer.email").exists())
                .andExpect(jsonPath("$.data.items").isArray());
    }

    @Test
    void getOrderDetails_notFound_returns404() throws Exception {
        mockMvc.perform(get("/orders/99999/details")
                        .cookie(adminCookie()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOrderDetails_asUser_returns403() throws Exception {
        Order order = createOrder(OrderStatus.PAID, BigDecimal.valueOf(200));

        mockMvc.perform(get("/orders/{orderId}/details", order.getId())
                        .cookie(userCookie()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getOrderDetails_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(get("/orders/99999/details"))
                .andExpect(status().isUnauthorized());
    }

    // ── PATCH /orders/{id}/status ─────────────────────────────────────────────

    @Test
    void updateOrderStatus_pendingToPaid_returns200() throws Exception {
        Order order = createOrder(OrderStatus.PENDING, BigDecimal.valueOf(100));

        mockMvc.perform(patch("/orders/{orderId}/status", order.getId())
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "PAID"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"));
    }

    @Test
    void updateOrderStatus_paidToShipped_returns200() throws Exception {
        Order order = createOrder(OrderStatus.PAID, BigDecimal.valueOf(100));

        mockMvc.perform(patch("/orders/{orderId}/status", order.getId())
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "SHIPPED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SHIPPED"));
    }

    @Test
    void updateOrderStatus_shippedToDelivered_returns200() throws Exception {
        Order order = createOrder(OrderStatus.SHIPPED, BigDecimal.valueOf(100));

        mockMvc.perform(patch("/orders/{orderId}/status", order.getId())
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "DELIVERED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DELIVERED"));
    }

    @Test
    void updateOrderStatus_pendingToDelivered_returns409() throws Exception {
        Order order = createOrder(OrderStatus.PENDING, BigDecimal.valueOf(100));

        mockMvc.perform(patch("/orders/{orderId}/status", order.getId())
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "DELIVERED"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Invalid order status transition: PENDING -> DELIVERED"));
    }

    @Test
    void updateOrderStatus_deliveredToShipped_returns409() throws Exception {
        Order order = createOrder(OrderStatus.DELIVERED, BigDecimal.valueOf(100));

        mockMvc.perform(patch("/orders/{orderId}/status", order.getId())
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "SHIPPED"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void updateOrderStatus_cancelledToPaid_returns409() throws Exception {
        Order order = createOrder(OrderStatus.CANCELLED, BigDecimal.valueOf(100));

        mockMvc.perform(patch("/orders/{orderId}/status", order.getId())
                        .cookie(adminCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "PAID"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void updateOrderStatus_asUser_returns403() throws Exception {
        Order order = createOrder(OrderStatus.SHIPPED, BigDecimal.valueOf(100));

        mockMvc.perform(patch("/orders/{orderId}/status", order.getId())
                        .cookie(userCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "DELIVERED"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    // ── DELETE /orders/{id} ───────────────────────────────────────────────────

    @Test
    void cancelOrder_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/orders/99999")
                        .cookie(userCookie()))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelOrder_pending_returns200AndCancelledStatus() throws Exception {
        Order order = createOrder(
                OrderStatus.PENDING,
                BigDecimal.valueOf(100)
        );

        mockMvc.perform(delete("/orders/{orderId}", order.getId())
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Order cancelled successfully"))
                .andExpect(jsonPath("$.data.status")
                        .value("CANCELLED"));
    }

    @Test
    void cancelOrder_paid_returns200AndCancelledStatus() throws Exception {
        Order order = createOrder(
                OrderStatus.PAID,
                BigDecimal.valueOf(100)
        );

        mockMvc.perform(delete("/orders/{orderId}", order.getId())
                        .cookie(userCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Order cancelled successfully"))
                .andExpect(jsonPath("$.data.status")
                        .value("CANCELLED"));
    }

    @Test
    void cancelOrder_shipped_returns409() throws Exception {
        Order order = createOrder(
                OrderStatus.SHIPPED,
                BigDecimal.valueOf(100)
        );

        mockMvc.perform(delete("/orders/{orderId}", order.getId())
                        .cookie(userCookie()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(
                                "Order cannot be cancelled, id="
                                        + order.getId()
                                        + ". Current status: SHIPPED"
                                        + ". Only PENDING and PAID orders can be cancelled"
                        ));
    }

    @Test
    void cancelOrder_delivered_returns409() throws Exception {
        Order order = createOrder(
                OrderStatus.DELIVERED,
                BigDecimal.valueOf(100)
        );

        mockMvc.perform(delete("/orders/{orderId}", order.getId())
                        .cookie(userCookie()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(
                                "Order cannot be cancelled, id="
                                        + order.getId()
                                        + ". Current status: DELIVERED"
                                        + ". Only PENDING and PAID orders can be cancelled"
                        ));
    }

    @Test
    void cancelOrder_alreadyCancelled_returns409() throws Exception {
        Order order = createOrder(
                OrderStatus.CANCELLED,
                BigDecimal.valueOf(100)
        );

        mockMvc.perform(delete("/orders/{orderId}", order.getId())
                        .cookie(userCookie()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(
                                "Order is already cancelled, id="
                                        + order.getId()
                        ));
    }

    @Test
    void cancelOrder_unauthenticated_returns401() throws Exception {
        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isUnauthorized());
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
