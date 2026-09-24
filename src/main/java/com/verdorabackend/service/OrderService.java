package com.verdorabackend.service;

import com.verdorabackend.dto.request.UpdateOrderStatusRequest;
import com.verdorabackend.dto.response.AdminOrderResponse;
import com.verdorabackend.dto.response.OrderResponse;
import com.verdorabackend.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(Long userId);

    List<OrderResponse> getOrders(Long userId);

    Page<AdminOrderResponse> getAllOrders(Pageable pageable);

    Page<AdminOrderResponse> getAllOrders(
            OrderStatus status,
            LocalDate dateFrom,
            LocalDate dateTo,
            Pageable pageable
    );

    OrderResponse getOrder(Long userId, Long orderId);

    OrderResponse cancelOrder(Long userId, Long orderId);

    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);
}
