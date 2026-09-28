package com.verdorabackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class ReportControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ── GET /reports/top-purchased ─────────────────────────────────────────────

    @Test
    void getTopPurchasedProducts_asAdmin_returnsProductsSortedByPurchasedQuantity()
            throws Exception {

        clearOrders();

        long firstProductId = createProduct("Top Product");
        long secondProductId = createProduct("Second Product");

        long paidOrderId = createOrder("PAID");
        long shippedOrderId = createOrder("SHIPPED");

        createOrderItem(paidOrderId, firstProductId, 2);
        createOrderItem(shippedOrderId, firstProductId, 4);
        createOrderItem(paidOrderId, secondProductId, 5);

        mockMvc.perform(get("/reports/top-purchased")
                        .cookie(adminCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].productId").value(firstProductId))
                .andExpect(jsonPath("$.data[0].productName").value("Top Product"))
                .andExpect(jsonPath("$.data[0].purchasedQuantity").value(6))
                .andExpect(jsonPath("$.data[1].productId").value(secondProductId))
                .andExpect(jsonPath("$.data[1].productName").value("Second Product"))
                .andExpect(jsonPath("$.data[1].purchasedQuantity").value(5));
    }

    @Test
    void getTopPurchasedProducts_ignoresPendingAndCancelledOrders()
            throws Exception {

        clearOrders();

        long productId = createProduct("Status Test Product");

        long paidOrderId = createOrder("PAID");
        long pendingOrderId = createOrder("PENDING");
        long cancelledOrderId = createOrder("CANCELLED");

        createOrderItem(paidOrderId, productId, 3);
        createOrderItem(pendingOrderId, productId, 50);
        createOrderItem(cancelledOrderId, productId, 100);

        mockMvc.perform(get("/reports/top-purchased")
                        .cookie(adminCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].productId").value(productId))
                .andExpect(jsonPath("$.data[0].purchasedQuantity").value(3));
    }

    @Test
    void getTopPurchasedProducts_returnsMaximumTenProducts()
            throws Exception {

        clearOrders();

        long orderId = createOrder("PAID");

        for (int i = 1; i <= 11; i++) {
            long productId = createProduct("Purchased Product " + i);
            createOrderItem(orderId, productId, i);
        }

        mockMvc.perform(get("/reports/top-purchased")
                        .cookie(adminCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(10))
                .andExpect(jsonPath("$.data[0].purchasedQuantity").value(11))
                .andExpect(jsonPath("$.data[9].purchasedQuantity").value(2));
    }

    @Test
    void getTopPurchasedProducts_noData_returnsEmptyList()
            throws Exception {

        clearOrders();

        mockMvc.perform(get("/reports/top-purchased")
                        .cookie(adminCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void getTopPurchasedProducts_asUser_returns403()
            throws Exception {

        mockMvc.perform(get("/reports/top-purchased")
                        .cookie(userCookie()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTopPurchasedProducts_withoutAuthentication_returns401()
            throws Exception {

        mockMvc.perform(get("/reports/top-purchased"))
                .andExpect(status().isUnauthorized());
    }

    // ── GET /reports/top-cancelled ─────────────────────────────────────────────

    @Test
    void getTopCancelledProducts_asAdmin_returnsProductsSortedByCancelledQuantity()
            throws Exception {

        clearOrders();

        long firstProductId = createProduct("Most Cancelled Product");
        long secondProductId = createProduct("Second Cancelled Product");

        long firstCancelledOrderId = createOrder("CANCELLED");
        long secondCancelledOrderId = createOrder("CANCELLED");
        long paidOrderId = createOrder("PAID");

        createOrderItem(firstCancelledOrderId, firstProductId, 4);
        createOrderItem(secondCancelledOrderId, firstProductId, 3);
        createOrderItem(firstCancelledOrderId, secondProductId, 5);

        createOrderItem(paidOrderId, secondProductId, 100);

        mockMvc.perform(get("/reports/top-cancelled")
                        .cookie(adminCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].productId").value(firstProductId))
                .andExpect(jsonPath("$.data[0].productName").value("Most Cancelled Product"))
                .andExpect(jsonPath("$.data[0].cancelledQuantity").value(7))
                .andExpect(jsonPath("$.data[1].productId").value(secondProductId))
                .andExpect(jsonPath("$.data[1].cancelledQuantity").value(5));
    }

    @Test
    void getTopCancelledProducts_returnsMaximumTenProducts()
            throws Exception {

        clearOrders();

        long orderId = createOrder("CANCELLED");

        for (int i = 1; i <= 11; i++) {
            long productId = createProduct("Cancelled Product " + i);
            createOrderItem(orderId, productId, i);
        }

        mockMvc.perform(get("/reports/top-cancelled")
                        .cookie(adminCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(10))
                .andExpect(jsonPath("$.data[0].cancelledQuantity").value(11))
                .andExpect(jsonPath("$.data[9].cancelledQuantity").value(2));
    }

    @Test
    void getTopCancelledProducts_noData_returnsEmptyList()
            throws Exception {

        clearOrders();

        mockMvc.perform(get("/reports/top-cancelled")
                        .cookie(adminCookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void getTopCancelledProducts_asUser_returns403()
            throws Exception {

        mockMvc.perform(get("/reports/top-cancelled")
                        .cookie(userCookie()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTopCancelledProducts_withoutAuthentication_returns401()
            throws Exception {

        mockMvc.perform(get("/reports/top-cancelled"))
                .andExpect(status().isUnauthorized());
    }

    // ── GET /reports/pending-payment ───────────────────────────────────────────

    @Test
    void getPendingPaymentOrders_asAdmin_returnsOldPendingOrdersSortedByCreatedAt()
            throws Exception {

        clearOrders();

        long oldestOrderId = createOrder("PENDING", 10);
        long newerOrderId = createOrder("PENDING", 8);

        createOrder("PENDING", 3);
        createOrder("PAID", 15);
        createOrder("CANCELLED", 20);

        mockMvc.perform(get("/reports/pending-payment")
                        .cookie(adminCookie())
                        .param("n", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].orderId").value(oldestOrderId))
                .andExpect(jsonPath("$.data[0].totalPrice").value(100))
                .andExpect(jsonPath("$.data[0].pendingDays").value(10))
                .andExpect(jsonPath("$.data[0].customer.id").value(2))
                .andExpect(jsonPath("$.data[1].orderId").value(newerOrderId))
                .andExpect(jsonPath("$.data[1].pendingDays").value(8));
    }

    @Test
    void getPendingPaymentOrders_noMatchingOrders_returnsEmptyList()
            throws Exception {

        clearOrders();

        createOrder("PENDING", 3);
        createOrder("PAID", 20);
        createOrder("CANCELLED", 20);

        mockMvc.perform(get("/reports/pending-payment")
                        .cookie(adminCookie())
                        .param("n", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void getPendingPaymentOrders_zeroDays_returns400()
            throws Exception {

        mockMvc.perform(get("/reports/pending-payment")
                        .cookie(adminCookie())
                        .param("n", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPendingPaymentOrders_negativeDays_returns400()
            throws Exception {

        mockMvc.perform(get("/reports/pending-payment")
                        .cookie(adminCookie())
                        .param("n", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPendingPaymentOrders_nonNumericDays_returns400()
            throws Exception {

        mockMvc.perform(get("/reports/pending-payment")
                        .cookie(adminCookie())
                        .param("n", "abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPendingPaymentOrders_withoutN_returns400()
            throws Exception {

        mockMvc.perform(get("/reports/pending-payment")
                        .cookie(adminCookie()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPendingPaymentOrders_asUser_returns403()
            throws Exception {

        mockMvc.perform(get("/reports/pending-payment")
                        .cookie(userCookie())
                        .param("n", "7"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPendingPaymentOrders_withoutAuthentication_returns401()
            throws Exception {

        mockMvc.perform(get("/reports/pending-payment")
                        .param("n", "7"))
                .andExpect(status().isUnauthorized());
    }

    // ── GET /reports/revenue ───────────────────────────────────────────────────

    @Test
    void getRevenueReport_groupByDay_returnsRevenueGroupedByDay()
            throws Exception {

        clearOrders();

        createOrder(
                "PAID",
                BigDecimal.valueOf(100),
                OffsetDateTime.parse("2026-09-10T10:00:00+03:00")
        );
        createOrder(
                "SHIPPED",
                BigDecimal.valueOf(200),
                OffsetDateTime.parse("2026-09-10T15:00:00+03:00")
        );
        createOrder(
                "DELIVERED",
                BigDecimal.valueOf(400),
                OffsetDateTime.parse("2026-09-11T12:00:00+03:00")
        );

        mockMvc.perform(get("/reports/revenue")
                        .cookie(adminCookie())
                        .param("dateFrom", "2026-09-10")
                        .param("dateTo", "2026-09-11")
                        .param("groupBy", "DAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].revenue").value(300))
                .andExpect(jsonPath("$.data[1].revenue").value(400));
    }

    @Test
    void getRevenueReport_groupByHour_returnsRevenueGroupedByHour()
            throws Exception {

        clearOrders();

        createOrder(
                "PAID",
                BigDecimal.valueOf(100),
                OffsetDateTime.parse("2026-09-10T10:15:00+03:00")
        );
        createOrder(
                "PAID",
                BigDecimal.valueOf(150),
                OffsetDateTime.parse("2026-09-10T10:45:00+03:00")
        );
        createOrder(
                "SHIPPED",
                BigDecimal.valueOf(300),
                OffsetDateTime.parse("2026-09-10T11:10:00+03:00")
        );

        mockMvc.perform(get("/reports/revenue")
                        .cookie(adminCookie())
                        .param("dateFrom", "2026-09-10")
                        .param("dateTo", "2026-09-10")
                        .param("groupBy", "HOUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].revenue").value(250))
                .andExpect(jsonPath("$.data[1].revenue").value(300));
    }

    @Test
    void getRevenueReport_groupByWeek_returnsRevenueGroupedByWeek()
            throws Exception {

        clearOrders();

        createOrder(
                "PAID",
                BigDecimal.valueOf(100),
                OffsetDateTime.parse("2026-09-07T10:00:00+03:00")
        );
        createOrder(
                "DELIVERED",
                BigDecimal.valueOf(200),
                OffsetDateTime.parse("2026-09-10T10:00:00+03:00")
        );
        createOrder(
                "PAID",
                BigDecimal.valueOf(500),
                OffsetDateTime.parse("2026-09-14T10:00:00+03:00")
        );

        mockMvc.perform(get("/reports/revenue")
                        .cookie(adminCookie())
                        .param("dateFrom", "2026-09-07")
                        .param("dateTo", "2026-09-14")
                        .param("groupBy", "WEEK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].revenue").value(300))
                .andExpect(jsonPath("$.data[1].revenue").value(500));
    }

    @Test
    void getRevenueReport_groupByMonth_returnsRevenueGroupedByMonth()
            throws Exception {

        clearOrders();

        createOrder(
                "PAID",
                BigDecimal.valueOf(100),
                OffsetDateTime.parse("2026-08-10T10:00:00+03:00")
        );
        createOrder(
                "SHIPPED",
                BigDecimal.valueOf(200),
                OffsetDateTime.parse("2026-08-20T10:00:00+03:00")
        );
        createOrder(
                "DELIVERED",
                BigDecimal.valueOf(500),
                OffsetDateTime.parse("2026-09-05T10:00:00+03:00")
        );

        mockMvc.perform(get("/reports/revenue")
                        .cookie(adminCookie())
                        .param("dateFrom", "2026-08-01")
                        .param("dateTo", "2026-09-30")
                        .param("groupBy", "MONTH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].revenue").value(300))
                .andExpect(jsonPath("$.data[1].revenue").value(500));
    }

    @Test
    void getRevenueReport_ignoresPendingAndCancelledOrders()
            throws Exception {

        clearOrders();

        OffsetDateTime date =
                OffsetDateTime.parse("2026-09-10T12:00:00+03:00");

        createOrder("PAID", BigDecimal.valueOf(100), date);
        createOrder("SHIPPED", BigDecimal.valueOf(200), date);
        createOrder("DELIVERED", BigDecimal.valueOf(300), date);

        createOrder("PENDING", BigDecimal.valueOf(1000), date);
        createOrder("CANCELLED", BigDecimal.valueOf(2000), date);

        mockMvc.perform(get("/reports/revenue")
                        .cookie(adminCookie())
                        .param("dateFrom", "2026-09-10")
                        .param("dateTo", "2026-09-10")
                        .param("groupBy", "DAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].revenue").value(600));
    }

    @Test
    void getRevenueReport_respectsDateRange()
            throws Exception {

        clearOrders();

        createOrder(
                "PAID",
                BigDecimal.valueOf(1000),
                OffsetDateTime.parse("2026-09-09T12:00:00+03:00")
        );
        createOrder(
                "PAID",
                BigDecimal.valueOf(200),
                OffsetDateTime.parse("2026-09-10T12:00:00+03:00")
        );
        createOrder(
                "PAID",
                BigDecimal.valueOf(300),
                OffsetDateTime.parse("2026-09-11T12:00:00+03:00")
        );
        createOrder(
                "PAID",
                BigDecimal.valueOf(2000),
                OffsetDateTime.parse("2026-09-12T12:00:00+03:00")
        );

        mockMvc.perform(get("/reports/revenue")
                        .cookie(adminCookie())
                        .param("dateFrom", "2026-09-10")
                        .param("dateTo", "2026-09-11")
                        .param("groupBy", "DAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].revenue").value(200))
                .andExpect(jsonPath("$.data[1].revenue").value(300));
    }

    @Test
    void getRevenueReport_noData_returnsEmptyList()
            throws Exception {

        clearOrders();

        mockMvc.perform(get("/reports/revenue")
                        .cookie(adminCookie())
                        .param("dateFrom", "2026-09-01")
                        .param("dateTo", "2026-09-30")
                        .param("groupBy", "DAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void getRevenueReport_dateFromAfterDateTo_returns400()
            throws Exception {

        mockMvc.perform(get("/reports/revenue")
                        .cookie(adminCookie())
                        .param("dateFrom", "2026-09-30")
                        .param("dateTo", "2026-09-01")
                        .param("groupBy", "DAY"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRevenueReport_invalidGroupBy_returns400()
            throws Exception {

        mockMvc.perform(get("/reports/revenue")
                        .cookie(adminCookie())
                        .param("dateFrom", "2026-09-01")
                        .param("dateTo", "2026-09-30")
                        .param("groupBy", "YEAR"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRevenueReport_invalidDate_returns400()
            throws Exception {

        mockMvc.perform(get("/reports/revenue")
                        .cookie(adminCookie())
                        .param("dateFrom", "invalid")
                        .param("dateTo", "2026-09-30")
                        .param("groupBy", "DAY"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRevenueReport_missingParameters_returns400()
            throws Exception {

        mockMvc.perform(get("/reports/revenue")
                        .cookie(adminCookie()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRevenueReport_asUser_returns403()
            throws Exception {

        mockMvc.perform(get("/reports/revenue")
                        .cookie(userCookie())
                        .param("dateFrom", "2026-09-01")
                        .param("dateTo", "2026-09-30")
                        .param("groupBy", "DAY"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getRevenueReport_withoutAuthentication_returns401()
            throws Exception {

        mockMvc.perform(get("/reports/revenue")
                        .param("dateFrom", "2026-09-01")
                        .param("dateTo", "2026-09-30")
                        .param("groupBy", "DAY"))
                .andExpect(status().isUnauthorized());
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private void clearOrders() {
        jdbcTemplate.update("DELETE FROM order_items");
        jdbcTemplate.update("DELETE FROM orders");
    }

    private long createProduct(String name) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            INSERT INTO products (
                                name,
                                price,
                                category_id,
                                image_url
                            )
                            VALUES (?, ?, ?, ?)
                            """,
                    new String[]{"product_id"}
            );

            statement.setString(1, name);
            statement.setBigDecimal(2, BigDecimal.valueOf(100));
            statement.setLong(3, 1L);
            statement.setString(4, "https://example.com/report-product.jpg");

            return statement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    private long createOrder(String status) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            INSERT INTO orders (
                                user_id,
                                total_price,
                                status
                            )
                            VALUES (?, ?, ?)
                            """,
                    new String[]{"order_id"}
            );

            statement.setLong(1, 2L);
            statement.setBigDecimal(2, BigDecimal.valueOf(100));
            statement.setString(3, status);

            return statement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    private long createOrder(String status, int daysAgo) {
        long orderId = createOrder(status);

        jdbcTemplate.update(
                """
                        UPDATE orders
                        SET created_at = ?
                        WHERE order_id = ?
                        """,
                OffsetDateTime.now().minusDays(daysAgo),
                orderId
        );

        return orderId;
    }

    private void createOrderItem(
            long orderId,
            long productId,
            int quantity
    ) {
        jdbcTemplate.update(
                """
                        INSERT INTO order_items (
                            order_id,
                            product_id,
                            quantity,
                            price_at_purchase
                        )
                        VALUES (?, ?, ?, ?)
                        """,
                orderId,
                productId,
                quantity,
                BigDecimal.valueOf(100)
        );
    }

    private long createOrder(
            String status,
            BigDecimal totalPrice,
            OffsetDateTime createdAt
    ) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            INSERT INTO orders (
                                user_id,
                                total_price,
                                status,
                                created_at
                            )
                            VALUES (?, ?, ?, ?)
                            """,
                    new String[]{"order_id"}
            );

            statement.setLong(1, 2L);
            statement.setBigDecimal(2, totalPrice);
            statement.setString(3, status);
            statement.setObject(4, createdAt);

            return statement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }
}
