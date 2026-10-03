package com.verdorabackend.service.impl;

import com.verdorabackend.dto.request.UpdateOrderStatusRequest;
import com.verdorabackend.dto.response.*;
import com.verdorabackend.entity.Cart;
import com.verdorabackend.entity.Order;
import com.verdorabackend.entity.OrderItem;
import com.verdorabackend.entity.OrderStatus;
import com.verdorabackend.exception.CartIsEmptyException;
import com.verdorabackend.exception.OrderCannotBeCancelledException;
import com.verdorabackend.exception.OrderNotFoundException;
import com.verdorabackend.mapper.OrderMapper;
import com.verdorabackend.repository.CartRepository;
import com.verdorabackend.repository.OrderRepository;
import com.verdorabackend.repository.specification.OrderSpecification;
import com.verdorabackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse placeOrder(Long userId) {
        log.debug("Placing order for userId={}", userId);

        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseThrow(CartIsEmptyException::new);

        if (cart.getItems().isEmpty()) {
            throw new CartIsEmptyException();
        }

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(Math.toIntExact(cartItem.getQuantity()));
            orderItem.setPriceAtPurchase(cartItem.getProduct().getEffectivePrice());
            return orderItem;
        }).toList();

        order.setItems(orderItems);

        BigDecimal totalPrice = orderItems.stream()
                .map(item -> item.getPriceAtPurchase()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(totalPrice);

        Order saved = orderRepository.save(order);

        // Очищаємо кошик після оформлення замовлення
        cart.getItems().clear();
        cartRepository.save(cart);

        log.info("Order placed, id={}, userId={}, total={}", saved.getId(), userId, totalPrice);
        return buildOrderResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(Long userId) {
        log.debug("Fetching orders for userId={}", userId);
        return orderRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::buildOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminOrderResponse> getAllOrders(Pageable pageable) {
        return getAllOrders(null, null, null, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminOrderResponse> getAllOrders(
            OrderStatus status,
            LocalDate dateFrom,
            LocalDate dateTo,
            Pageable pageable
    ) {
        log.debug(
                "Fetching admin orders: status={}, dateFrom={}, dateTo={}, pageable={}",
                status, dateFrom, dateTo, pageable
        );

        Specification<Order> specification =
                OrderSpecification.filter(status, dateFrom, dateTo);

        return orderRepository.findAll(specification, pageable)
                .map(this::buildAdminOrderResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long userId, Long orderId) {
        log.debug("Fetching orderId={} for userId={}", orderId, userId);
        Order order = orderRepository.findByIdAndUser_Id(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return buildOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        log.debug("Cancelling orderId={} for userId={}", orderId, userId);
        Order order = orderRepository.findByIdAndUser_Id(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderCannotBeCancelledException(orderId);
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);
        log.info("Order cancelled, id={}", orderId);
        return buildOrderResponse(saved);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        log.debug("Updating status for orderId={} to {}", orderId, request.status());
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setStatus(request.status());
        Order saved = orderRepository.save(order);
        log.info("Order status updated, id={}, status={}", orderId, request.status());
        return buildOrderResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminOrderDetailsResponse getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return buildAdminOrderDetailsResponse(order);
    }

    private AdminOrderDetailsResponse buildAdminOrderDetailsResponse(Order order) {
        OrderResponse orderResponse = buildOrderResponse(order);
        AdminOrderResponse adminOrderResponse = buildAdminOrderResponse(order);

        return new AdminOrderDetailsResponse(
                orderResponse.orderId(),
                orderResponse.createdAt(),
                orderResponse.totalPrice(),
                orderResponse.status(),
                adminOrderResponse.customer(),
                orderResponse.items()
        );
    }

    private AdminOrderResponse buildAdminOrderResponse(Order order) {
        UserResponse customer = new UserResponse(
                order.getUser().getId(),
                order.getUser().getName(),
                order.getUser().getEmail(),
                order.getUser().getPhoneNumber()
        );

        return new AdminOrderResponse(
                order.getId(),
                order.getCreatedAt(),
                order.getTotalPrice(),
                order.getStatus(),
                customer
        );
    }

    private OrderResponse buildOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> {
                    OrderItemResponse base = orderMapper.toOrderItemResponse(item);
                    BigDecimal subtotal = item.getPriceAtPurchase()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                    return new OrderItemResponse(
                            base.orderItemId(),
                            base.productId(),
                            base.productName(),
                            base.quantity(),
                            base.priceAtPurchase(),
                            subtotal
                    );
                })
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCreatedAt(),
                order.getTotalPrice(),
                order.getStatus(),
                itemResponses
        );
    }
}
