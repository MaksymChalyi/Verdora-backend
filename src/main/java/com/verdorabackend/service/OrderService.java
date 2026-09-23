package com.verdorabackend.service;

import com.verdorabackend.dto.request.UpdateOrderStatusRequest;
import com.verdorabackend.dto.response.OrderResponse;

import com.verdorabackend.dto.response.AdminOrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(Long userId);

    List<OrderResponse> getOrders(Long userId);

    Page<AdminOrderResponse> getAllOrders(Pageable pageable);

    OrderResponse getOrder(Long userId, Long orderId);

    OrderResponse cancelOrder(Long userId, Long orderId);

    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);
}
