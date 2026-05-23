package com.verdorabackend.service.impl;

import com.verdorabackend.dto.request.AddToCartRequest;
import com.verdorabackend.dto.request.UpdateCartItemRequest;
import com.verdorabackend.dto.response.CartItemResponse;
import com.verdorabackend.dto.response.CartResponse;
import com.verdorabackend.entity.Cart;
import com.verdorabackend.entity.CartItem;
import com.verdorabackend.entity.Product;
import com.verdorabackend.entity.User;
import com.verdorabackend.exception.CartItemNotFoundException;
import com.verdorabackend.exception.ProductNotFoundException;
import com.verdorabackend.mapper.CartMapper;
import com.verdorabackend.repository.CartItemRepository;
import com.verdorabackend.repository.CartRepository;
import com.verdorabackend.repository.ProductRepository;
import com.verdorabackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(BigDecimal.valueOf(1000));

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2L);
    }

    // ── getCart ──────────────────────────────────────────────────────────────

    @Test
    void getCart_noCart_createsNewCart() {
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.save(any())).thenReturn(cart);

        CartResponse result = cartService.getCart(1L);

        assertThat(result).isNotNull();
        verify(cartRepository).save(any(Cart.class));
    }

    // ── addItem ──────────────────────────────────────────────────────────────

    @Test
    void addItem_newProduct_addsItem() {
        AddToCartRequest request = new AddToCartRequest(1L, 2L);

        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCart_IdAndProduct_Id(1L, 1L)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any())).thenReturn(cartItem);
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));

        cartService.addItem(1L, request);

        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void addItem_productNotFound_throwsException() {
        AddToCartRequest request = new AddToCartRequest(99L, 1L);

        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addItem(1L, request))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // ── updateItem ───────────────────────────────────────────────────────────

    @Test
    void updateItem_itemNotFound_throwsException() {
        UpdateCartItemRequest request = new UpdateCartItemRequest(5L);

        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.updateItem(1L, 99L, request))
                .isInstanceOf(CartItemNotFoundException.class);
    }

    // ── removeItem ───────────────────────────────────────────────────────────

    @Test
    void removeItem_itemNotFound_throwsException() {
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.removeItem(1L, 99L))
                .isInstanceOf(CartItemNotFoundException.class);
    }

    // ── clearCart ────────────────────────────────────────────────────────────

    @Test
    void clearCart_clearsAllItems() {
        cart.getItems().add(cartItem);

        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenReturn(cart);

        cartService.clearCart(1L);

        assertThat(cart.getItems()).isEmpty();
        verify(cartRepository).save(cart);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private CartItemResponse mockCartItemResponse() {
        return new CartItemResponse(1L, 1L, "Laptop", "https://img.url", BigDecimal.valueOf(1000), 2L, BigDecimal.valueOf(2000));
    }
}
