package com.verdorabackend.service;

import com.verdorabackend.dto.request.UpdateOrderStatusRequest;
import com.verdorabackend.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(Long userId);

    List<OrderResponse> getOrders(Long userId);

    OrderResponse getOrder(Long userId, Long orderId);

    OrderResponse cancelOrder(Long userId, Long orderId);

    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);
}
