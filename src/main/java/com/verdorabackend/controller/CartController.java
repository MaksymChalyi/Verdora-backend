package com.verdorabackend.controller;

import com.verdorabackend.dto.request.AddToCartRequest;
import com.verdorabackend.dto.request.UpdateCartItemRequest;
import com.verdorabackend.dto.response.BaseResponse;
import com.verdorabackend.dto.response.BaseResponseFactory;
import com.verdorabackend.dto.response.CartResponse;
import com.verdorabackend.security.UserPrincipal;
import com.verdorabackend.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cart", description = "Endpoints for managing the shopping cart")
@RequestMapping("/cart")
@SecurityRequirement(name = "Cookie-based Authentication")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Get cart", description = "Returns current user's cart with all items and total price")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cart returned",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-05-23T12:00:00Z",
                                      "status": 200,
                                      "message": "Cart fetched successfully",
                                      "data": {
                                        "cartId": 1,
                                        "items": [
                                          {
                                            "cartItemId": 1,
                                            "productId": 5,
                                            "productName": "Laptop",
                                            "price": 500.00,
                                            "quantity": 2,
                                            "subtotal": 1000.00
                                          }
                                        ],
                                        "totalPrice": 1000.00
                                      }
                                    }
                                    """)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<BaseResponse<CartResponse>> getCart(
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("Request to get cart for userId={}", principal.getUser().getId());

        CartResponse response = cartService.getCart(principal.getUser().getId());

        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Cart fetched successfully", response)
        );
    }

    @Operation(summary = "Add item to cart", description = "Adds a product to the cart. If already present, increases quantity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item added to cart"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping("/items")
    public ResponseEntity<BaseResponse<CartResponse>> addItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid AddToCartRequest request) {
        log.info("Request to add productId={} to cart for userId={}",
                request.productId(), principal.getUser().getId());

        CartResponse response = cartService.addItem(principal.getUser().getId(), request);

        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Item added to cart", response)
        );
    }

    @Operation(summary = "Update cart item quantity", description = "Updates the quantity of a specific cart item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart item updated"),
            @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<BaseResponse<CartResponse>> updateItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long cartItemId,
            @RequestBody @Valid UpdateCartItemRequest request) {
        log.info("Request to update cartItemId={} for userId={}", cartItemId, principal.getUser().getId());

        CartResponse response = cartService.updateItem(
                principal.getUser().getId(), cartItemId, request);

        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Cart item updated", response)
        );
    }

    @Operation(summary = "Remove item from cart", description = "Removes a specific item from the cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart item removed"),
            @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<BaseResponse<CartResponse>> removeItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long cartItemId) {
        log.info("Request to remove cartItemId={} for userId={}", cartItemId, principal.getUser().getId());

        CartResponse response = cartService.removeItem(principal.getUser().getId(), cartItemId);

        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Cart item removed", response)
        );
    }

    @Operation(summary = "Clear cart", description = "Removes all items from the cart")
    @ApiResponse(responseCode = "200", description = "Cart cleared")
    @DeleteMapping
    public ResponseEntity<BaseResponse<Void>> clearCart(
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("Request to clear cart for userId={}", principal.getUser().getId());

        cartService.clearCart(principal.getUser().getId());

        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Cart cleared successfully")
        );
    }
}
