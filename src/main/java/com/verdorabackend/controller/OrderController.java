package com.verdorabackend.controller;

import com.verdorabackend.dto.request.UpdateOrderStatusRequest;
import com.verdorabackend.dto.response.BaseResponse;
import com.verdorabackend.dto.response.BaseResponseFactory;
import com.verdorabackend.dto.response.OrderResponse;
import com.verdorabackend.security.UserPrincipal;
import com.verdorabackend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orders", description = "Endpoints for managing orders")
@RequestMapping("/orders")
@SecurityRequirement(name = "Cookie-based Authentication")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Place order", description = "Creates an order from current cart and clears the cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order placed"),
            @ApiResponse(responseCode = "400", description = "Cart is empty")
    })
    @PostMapping
    public ResponseEntity<BaseResponse<OrderResponse>> placeOrder(
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("Request to place order for userId={}", principal.getUser().getId());

        OrderResponse response = orderService.placeOrder(principal.getUser().getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponseFactory.success(HttpStatus.CREATED, "Order placed successfully", response)
        );
    }

    @Operation(summary = "Get all orders", description = "Returns all orders of the current user, newest first")
    @ApiResponse(responseCode = "200", description = "Orders returned")
    @GetMapping
    public ResponseEntity<BaseResponse<List<OrderResponse>>> getOrders(
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("Request to get orders for userId={}", principal.getUser().getId());

        List<OrderResponse> response = orderService.getOrders(principal.getUser().getId());

        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Orders fetched successfully", response)
        );
    }

    @Operation(summary = "Get order by ID", description = "Returns a specific order of the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order returned"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<BaseResponse<OrderResponse>> getOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long orderId) {
        log.info("Request to get orderId={} for userId={}", orderId, principal.getUser().getId());

        OrderResponse response = orderService.getOrder(principal.getUser().getId(), orderId);

        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Order fetched successfully", response)
        );
    }

    @Operation(summary = "Cancel order", description = "Cancels an order. Only PENDING orders can be cancelled")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Order cannot be cancelled")
    })
    @DeleteMapping("/{orderId}")
    public ResponseEntity<BaseResponse<OrderResponse>> cancelOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long orderId) {
        log.info("Request to cancel orderId={} for userId={}", orderId, principal.getUser().getId());

        OrderResponse response = orderService.cancelOrder(principal.getUser().getId(), orderId);

        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Order cancelled successfully", response)
        );
    }

    @Operation(summary = "Update order status", description = "Updates order status. ADMIN only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<BaseResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody @Valid UpdateOrderStatusRequest request) {
        log.info("Request to update status for orderId={} to {}", orderId, request.status());

        OrderResponse response = orderService.updateOrderStatus(orderId, request);

        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Order status updated", response)
        );
    }
}
