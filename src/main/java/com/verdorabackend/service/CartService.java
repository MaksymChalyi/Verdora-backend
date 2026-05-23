package com.verdorabackend.service;

import com.verdorabackend.dto.request.AddToCartRequest;
import com.verdorabackend.dto.request.UpdateCartItemRequest;
import com.verdorabackend.dto.response.CartResponse;

public interface CartService {

    CartResponse getCart(Long userId);

    CartResponse addItem(Long userId, AddToCartRequest request);

    CartResponse updateItem(Long userId, Long cartItemId, UpdateCartItemRequest request);

    CartResponse removeItem(Long userId, Long cartItemId);

    void clearCart(Long userId);
}
