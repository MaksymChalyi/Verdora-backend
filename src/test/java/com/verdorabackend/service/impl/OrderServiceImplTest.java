package com.verdorabackend.service.impl;

import com.verdorabackend.dto.request.UpdateOrderStatusRequest;
import com.verdorabackend.dto.response.AdminOrderDetailsResponse;
import com.verdorabackend.dto.response.AdminOrderResponse;
import com.verdorabackend.dto.response.OrderResponse;
import com.verdorabackend.entity.*;
import com.verdorabackend.exception.CartIsEmptyException;
import com.verdorabackend.exception.OrderCannotBeCancelledException;
import com.verdorabackend.exception.OrderNotFoundException;
import com.verdorabackend.mapper.OrderMapper;
import com.verdorabackend.repository.CartRepository;
import com.verdorabackend.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;
    private Order order;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("user@verdora.com");
        user.setPhoneNumber("+380501234567");

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(BigDecimal.valueOf(1000));

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(2L);

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>(List.of(cartItem)));
        cartItem.setCart(cart);

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(BigDecimal.valueOf(2000));
        order.setCreatedAt(OffsetDateTime.now());
        order.setItems(new ArrayList<>());
    }

    // ── placeOrder ───────────────────────────────────────────────────────────

    @Test
    void placeOrder_validCart_createsOrder() {
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any())).thenReturn(order);
        when(cartRepository.save(any())).thenReturn(cart);

        orderService.placeOrder(1L);

        verify(orderRepository).save(any(Order.class));
        verify(cartRepository).save(cart);
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void placeOrder_emptyCart_throwsException() {
        cart.getItems().clear();
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> orderService.placeOrder(1L))
                .isInstanceOf(CartIsEmptyException.class);
    }

    @Test
    void placeOrder_noCart_throwsException() {
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.placeOrder(1L))
                .isInstanceOf(CartIsEmptyException.class);
    }

    @Test
    void placeOrder_fixesPriceAtPurchase() {
        product.setPrice(BigDecimal.valueOf(999));
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any())).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.getItems().forEach(item ->
                    assertThat(item.getPriceAtPurchase()).isEqualByComparingTo(BigDecimal.valueOf(999))
            );
            return order;
        });
        when(cartRepository.save(any())).thenReturn(cart);

        orderService.placeOrder(1L);
    }

    // ── getOrders ────────────────────────────────────────────────────────────

    @Test
    void getOrders_returnsUserOrders() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orders = new PageImpl<>(List.of(order), pageable, 1);
        when(orderRepository.findByUser_Id(1L, pageable)).thenReturn(orders);

        Page<OrderResponse> result = orderService.getOrders(1L, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(orderRepository).findByUser_Id(1L, pageable);
    }

    @Test
    void getOrders_noOrders_returnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.DESC, "createdAt"));
        when(orderRepository.findByUser_Id(1L, pageable)).thenReturn(Page.empty(pageable));

        Page<OrderResponse> result = orderService.getOrders(1L, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    // ── getAllOrders ─────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void getAllOrders_returnsOrdersPage() {
        Pageable pageable = PageRequest.of(0, 12);

        Page<Order> orders = new PageImpl<>(
                List.of(order),
                pageable,
                1
        );

        when(orderRepository.findAll(any(Specification.class),
                eq(pageable))).thenReturn(orders);

        Page<AdminOrderResponse> result = orderService.getAllOrders(
                OrderStatus.PENDING,
                LocalDate.now().minusDays(7),
                LocalDate.now(),
                pageable
        );

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).orderId())
                .isEqualTo(1L);
        assertThat(result.getContent().get(0).status())
                .isEqualTo(OrderStatus.PENDING);
        assertThat(result.getContent().get(0).customer().id())
                .isEqualTo(1L);
        assertThat(result.getContent().get(0).customer().email())
                .isEqualTo("user@verdora.com");

        verify(orderRepository).findAll(
                any(Specification.class),
                eq(pageable)
        );
    }

    // ── getOrder ─────────────────────────────────────────────────────────────

    @Test
    void getOrder_existingOrder_returnsOrder() {
        when(orderRepository.findByIdAndUser_Id(1L, 1L)).thenReturn(Optional.of(order));

        orderService.getOrder(1L, 1L);

        verify(orderRepository).findByIdAndUser_Id(1L, 1L);
    }

    @Test
    void getOrder_notFound_throwsException() {
        when(orderRepository.findByIdAndUser_Id(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(1L, 99L))
                .isInstanceOf(OrderNotFoundException.class);
    }

    // ── cancelOrder ──────────────────────────────────────────────────────────

    @Test
    void cancelOrder_pendingOrder_cancels() {
        order.setStatus(OrderStatus.PENDING);
        when(orderRepository.findByIdAndUser_Id(1L, 1L)).thenReturn(Optional.of(order));
        OrderResponse response = orderService.cancelOrder(1L, 1L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(response.status()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository).findByIdAndUser_Id(1L, 1L);
    }

    @Test
    void cancelOrder_paidOrder_cancels() {
        order.setStatus(OrderStatus.PAID);

        when(orderRepository.findByIdAndUser_Id(1L, 1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.cancelOrder(1L, 1L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(response.status()).isEqualTo(OrderStatus.CANCELLED);

        verify(orderRepository).findByIdAndUser_Id(1L, 1L);
    }

    @Test
    void cancelOrder_shippedOrder_throwsException() {
        order.setStatus(OrderStatus.SHIPPED);

        when(orderRepository.findByIdAndUser_Id(1L, 1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L, 1L)).isInstanceOf(OrderCannotBeCancelledException.class);
    }

    @Test
    void cancelOrder_deliveredOrder_throwsException() {
        order.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findByIdAndUser_Id(1L, 1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L, 1L))
                .isInstanceOf(OrderCannotBeCancelledException.class);
    }

    @Test
    void cancelOrder_alreadyCancelled_throwsException() {
        order.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findByIdAndUser_Id(1L, 1L))
                .thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L, 1L))
                .isInstanceOf(OrderCannotBeCancelledException.class);
    }

    @Test
    void cancelOrder_notFound_throwsException() {
        when(orderRepository.findByIdAndUser_Id(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancelOrder(1L, 99L))
                .isInstanceOf(OrderNotFoundException.class);
    }

    // ── updateOrderStatus ────────────────────────────────────────────────────

    @Test
    void updateOrderStatus_validRequest_updatesStatus() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.SHIPPED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        OrderResponse response = orderService.updateOrderStatus(1L, request);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        assertThat(response.status()).isEqualTo(OrderStatus.SHIPPED);
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_orderNotFound_throwsException() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.PAID);
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrderStatus(99L, request)).isInstanceOf(OrderNotFoundException.class);
    }

    // ── getOrderDetails ──────────────────────────────────────────────────────

    @Test
    void getOrderDetails_existingOrder_returnsDetails() {
        order.setStatus(OrderStatus.PAID);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        AdminOrderDetailsResponse result =
                orderService.getOrderDetails(1L);

        assertThat(result.orderId())
                .isEqualTo(1L);

        assertThat(result.status())
                .isEqualTo(OrderStatus.PAID);

        assertThat(result.totalPrice())
                .isEqualByComparingTo(BigDecimal.valueOf(2000));

        assertThat(result.customer().id())
                .isEqualTo(1L);

        assertThat(result.customer().name())
                .isEqualTo("Test User");

        assertThat(result.customer().email())
                .isEqualTo("user@verdora.com");

        assertThat(result.customer().phone())
                .isEqualTo("+380501234567");

        assertThat(result.items())
                .isEmpty();
    }

    @Test
    void getOrderDetails_notFound_throwsException() {
        when(orderRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> orderService.getOrderDetails(99L)
        ).isInstanceOf(OrderNotFoundException.class);
    }
}
