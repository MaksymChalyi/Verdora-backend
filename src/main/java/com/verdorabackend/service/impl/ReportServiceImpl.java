package com.verdorabackend.service.impl;

import com.verdorabackend.dto.response.*;
import com.verdorabackend.entity.Order;
import com.verdorabackend.entity.OrderStatus;
import com.verdorabackend.repository.OrderItemRepository;
import com.verdorabackend.repository.OrderRepository;
import com.verdorabackend.service.ReportService;

import java.time.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private static final int TOP_PRODUCTS_LIMIT = 10;

    private static final Set<OrderStatus> PURCHASED_STATUSES = Set.of(
            OrderStatus.PAID,
            OrderStatus.SHIPPED,
            OrderStatus.DELIVERED
    );

    private static final Set<OrderStatus> REVENUE_STATUSES = Set.of(
            OrderStatus.PAID,
            OrderStatus.SHIPPED,
            OrderStatus.DELIVERED
    );

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TopPurchasedProductResponse> getTopPurchasedProducts() {
        log.debug("Fetching top purchased products");

        return orderItemRepository.findTopPurchasedProducts(
                PURCHASED_STATUSES,
                PageRequest.of(0, TOP_PRODUCTS_LIMIT)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopCancelledProductResponse> getTopCancelledProducts() {
        log.debug("Fetching top cancelled products");

        return orderItemRepository.findTopCancelledProducts(
                OrderStatus.CANCELLED,
                PageRequest.of(0, TOP_PRODUCTS_LIMIT)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendingPaymentOrderResponse> getPendingPaymentOrders(int days) {
        log.debug("Fetching orders pending payment longer than {} days", days);

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime cutoff = now.minusDays(days);

        return orderRepository
                .findByStatusAndCreatedAtBeforeOrderByCreatedAtAsc(
                        OrderStatus.PENDING,
                        cutoff
                )
                .stream()
                .map(order -> toPendingPaymentResponse(order, now))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevenueReportResponse> getRevenueReport(
            LocalDate dateFrom,
            LocalDate dateTo,
            String groupBy
    ) {
        log.debug(
                "Fetching revenue report from {} to {}, groupBy={}",
                dateFrom,
                dateTo,
                groupBy
        );

        ZoneId zone = ZoneId.systemDefault();

        OffsetDateTime from = dateFrom
                .atStartOfDay(zone)
                .toOffsetDateTime();

        OffsetDateTime toExclusive = dateTo
                .plusDays(1)
                .atStartOfDay(zone)
                .toOffsetDateTime();

        List<Order> orders =
                orderRepository
                        .findByStatusInAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtAsc(
                                REVENUE_STATUSES,
                                from,
                                toExclusive
                        );

        Map<OffsetDateTime, BigDecimal> revenueByPeriod = new TreeMap<>();

        for (Order order : orders) {
            OffsetDateTime periodStart =
                    truncateToPeriod(order.getCreatedAt(), groupBy);

            revenueByPeriod.merge(
                    periodStart,
                    order.getTotalPrice(),
                    BigDecimal::add
            );
        }

        return revenueByPeriod.entrySet()
                .stream()
                .map(entry -> new RevenueReportResponse(
                        entry.getKey(),
                        entry.getValue()
                ))
                .toList();
    }

    private OffsetDateTime truncateToPeriod(
            OffsetDateTime dateTime,
            String groupBy
    ) {
        return switch (groupBy) {
            case "HOUR" -> dateTime
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);

            case "DAY" -> dateTime
                    .toLocalDate()
                    .atStartOfDay()
                    .atOffset(dateTime.getOffset());

            case "WEEK" -> dateTime
                    .toLocalDate()
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    .atStartOfDay()
                    .atOffset(dateTime.getOffset());

            case "MONTH" -> dateTime
                    .toLocalDate()
                    .withDayOfMonth(1)
                    .atStartOfDay()
                    .atOffset(dateTime.getOffset());

            default -> throw new IllegalArgumentException(
                    "Unsupported groupBy: " + groupBy
            );
        };
    }

    private PendingPaymentOrderResponse toPendingPaymentResponse(Order order, OffsetDateTime now) {
        UserResponse customer = new UserResponse(
                order.getUser().getId(),
                order.getUser().getName(),
                order.getUser().getEmail(),
                order.getUser().getPhoneNumber()
        );

        long pendingDays = Duration.between(order.getCreatedAt(), now).toDays();

        return new PendingPaymentOrderResponse(
                order.getId(),
                order.getCreatedAt(),
                order.getTotalPrice(),
                pendingDays,
                customer
        );
    }

}
