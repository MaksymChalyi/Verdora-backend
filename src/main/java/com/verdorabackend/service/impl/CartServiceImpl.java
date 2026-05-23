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
import com.verdorabackend.exception.UserNotFoundException;
import com.verdorabackend.mapper.CartMapper;
import com.verdorabackend.repository.CartItemRepository;
import com.verdorabackend.repository.CartRepository;
import com.verdorabackend.repository.ProductRepository;
import com.verdorabackend.repository.UserRepository;
import com.verdorabackend.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public CartResponse getCart(Long userId) {
        log.debug("Fetching cart for userId={}", userId);
        Cart cart = getOrCreateCart(userId);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItem(Long userId, AddToCartRequest request) {
        log.debug("Adding product={} to cart for userId={}", request.productId(), userId);

        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(request.productId()));

        Optional<CartItem> existing = cartItemRepository
                .findByCart_IdAndProduct_Id(cart.getId(), product.getId());

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + request.quantity());
            cartItemRepository.save(item);
            log.info("Updated quantity for product={} in cart={}", product.getId(), cart.getId());
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.quantity());
            cartItemRepository.save(newItem);
            log.info("Added product={} to cart={}", product.getId(), cart.getId());
        }

        Cart updatedCart = cartRepository.findByUser_Id(userId).orElseThrow();
        return buildCartResponse(updatedCart);
    }

    @Override
    @Transactional
    public CartResponse updateItem(Long userId, Long cartItemId, UpdateCartItemRequest request) {
        log.debug("Updating cartItemId={} for userId={}", cartItemId, userId);

        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));

        validateItemBelongsToCart(item, cart);

        item.setQuantity(request.quantity());
        cartItemRepository.save(item);
        log.info("CartItem={} updated, new quantity={}", cartItemId, request.quantity());

        Cart updatedCart = cartRepository.findByUser_Id(userId).orElseThrow();
        return buildCartResponse(updatedCart);
    }

    @Override
    @Transactional
    public CartResponse removeItem(Long userId, Long cartItemId) {
        log.debug("Removing cartItemId={} for userId={}", cartItemId, userId);

        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));

        validateItemBelongsToCart(item, cart);

        cartItemRepository.delete(item);
        log.info("CartItem={} removed from cart={}", cartItemId, cart.getId());

        Cart updatedCart = cartRepository.findByUser_Id(userId).orElseThrow();
        return buildCartResponse(updatedCart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        log.debug("Clearing cart for userId={}", userId);
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
        log.info("Cart cleared for userId={}", userId);
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUser_Id(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(UserNotFoundException::new);
            Cart cart = new Cart();
            cart.setUser(user);
            Cart saved = cartRepository.save(cart);
            log.info("Created new cart for userId={}", userId);
            return saved;
        });
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> {
                    CartItemResponse base = cartMapper.toCartItemResponse(item);
                    BigDecimal subtotal = item.getProduct().getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                    return new CartItemResponse(
                            base.cartItemId(),
                            base.productId(),
                            base.productName(),
                            base.imageUrl(),
                            base.price(),
                            base.quantity(),
                            subtotal
                    );
                })
                .toList();

        BigDecimal totalPrice = itemResponses.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(cart.getId(), itemResponses, totalPrice);
    }

    private void validateItemBelongsToCart(CartItem item, Cart cart) {
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new CartItemNotFoundException(item.getId());
        }
    }
}