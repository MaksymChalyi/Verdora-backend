package com.verdorabackend.controller;

import com.verdorabackend.dto.request.UpdateOrderStatusRequest;
import com.verdorabackend.dto.response.*;
import com.verdorabackend.entity.OrderStatus;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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

    @Operation(
            summary = "Get current user's order history",
            description = "Returns paginated order history of the currently authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    @GetMapping
    public ResponseEntity<BaseResponse<Page<OrderResponse>>> getOrders(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long userId = principal.getUser().getId();
        log.info("Request to get orders for userId={}, page={}, size={}", userId, pageable.getPageNumber(), pageable.getPageSize());
        Pageable limitedPageable = PageRequest.of(pageable.getPageNumber(), Math.min(pageable.getPageSize(), 12), pageable.getSort());
        Page<OrderResponse> response = orderService.getOrders(userId, limitedPageable);
        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Orders fetched successfully", response)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all orders", description = "Returns all orders with optional filters")
    @ApiResponse(responseCode = "200", description = "Orders returned")
    @GetMapping("/all")
    public ResponseEntity<BaseResponse<Page<AdminOrderResponse>>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("Request to get all orders");
        Page<AdminOrderResponse> response = orderService.getAllOrders(status, dateFrom, dateTo, pageable);
        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "Orders fetched successfully",
                        response
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get order details",
            description = "Returns full order details for admin"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order details returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{orderId}/details")
    public ResponseEntity<BaseResponse<AdminOrderDetailsResponse>> getOrderDetails(
            @PathVariable Long orderId) {

        log.info("Admin request to get order details, orderId={}", orderId);

        AdminOrderDetailsResponse response = orderService.getOrderDetails(orderId);

        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "Order details fetched successfully",
                        response
                )
        );
    }

    @Operation(summary = "Get order status", description = "Returns the current status of the authenticated user's order. The endpoint is intended for lightweight polling.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order status returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    @GetMapping("/{orderId}/status")
    public ResponseEntity<BaseResponse<OrderStatusResponse>> getOrderStatus(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long orderId) {
        Long userId = principal.getUser().getId();
        log.debug("Request to get status for orderId={}, userId={}", orderId, userId);
        OrderStatusResponse response = orderService.getOrderStatus(userId, orderId);

        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "Order status fetched successfully",
                        response
                )
        );
    }

    @Operation(summary = "Get order details", description = "Returns full details of a specific order belonging to the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order details returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<BaseResponse<OrderResponse>> getOrder(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long orderId) {
        Long userId = principal.getUser().getId();
        log.info("Request to get orderId={} for userId={}", orderId, userId);
        OrderResponse response = orderService.getOrder(userId, orderId);
        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Order fetched successfully", response)
        );
    }

    @Operation(
            summary = "Cancel order",
            description = "Cancels the current user's order if it is in PENDING or PAID status"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order cancelled successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Order cannot be cancelled in its current status"
            )
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
