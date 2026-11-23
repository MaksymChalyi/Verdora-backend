package com.verdorabackend.service;

import com.verdorabackend.dto.request.CheckoutRequest;
import com.verdorabackend.dto.request.UpdateOrderStatusRequest;
import com.verdorabackend.dto.response.AdminOrderDetailsResponse;
import com.verdorabackend.dto.response.AdminOrderResponse;
import com.verdorabackend.dto.response.CheckoutSummaryResponse;
import com.verdorabackend.dto.response.OrderResponse;
import com.verdorabackend.dto.response.OrderStatusResponse;
import com.verdorabackend.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface OrderService {

    CheckoutSummaryResponse getCheckoutSummary(Long userId, CheckoutRequest request);

    OrderResponse placeOrder(Long userId, CheckoutRequest request);

    Page<OrderResponse> getOrders(Long userId, Pageable pageable);

    Page<AdminOrderResponse> getAllOrders(
            OrderStatus status,
            LocalDate dateFrom,
            LocalDate dateTo,
            Pageable pageable
    );

    OrderResponse getOrder(Long userId, Long orderId);

    OrderResponse cancelOrder(Long userId, Long orderId);

    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);

    AdminOrderDetailsResponse getOrderDetails(Long orderId);

    OrderStatusResponse getOrderStatus(Long userId, Long orderId);
}
