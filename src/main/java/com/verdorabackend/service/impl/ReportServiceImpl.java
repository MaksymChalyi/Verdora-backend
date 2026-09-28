package com.verdorabackend.service.impl;

import com.verdorabackend.dto.response.TopCancelledProductResponse;
import com.verdorabackend.dto.response.TopPurchasedProductResponse;
import com.verdorabackend.entity.OrderStatus;
import com.verdorabackend.repository.OrderItemRepository;
import com.verdorabackend.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
