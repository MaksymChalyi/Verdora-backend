package com.verdorabackend.service.impl;

import com.verdorabackend.dto.response.PendingPaymentOrderResponse;
import com.verdorabackend.dto.response.TopCancelledProductResponse;
import com.verdorabackend.dto.response.TopPurchasedProductResponse;
import com.verdorabackend.dto.response.UserResponse;
import com.verdorabackend.entity.Order;
import com.verdorabackend.entity.OrderStatus;
import com.verdorabackend.repository.OrderItemRepository;
import com.verdorabackend.repository.OrderRepository;
import com.verdorabackend.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

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
