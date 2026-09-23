package com.verdorabackend.controller;

import com.verdorabackend.dto.response.AdminOrderResponse;
import com.verdorabackend.dto.response.BaseResponse;
import com.verdorabackend.dto.response.BaseResponseFactory;
import com.verdorabackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private static final int MAX_PAGE_SIZE = 12;

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<AdminOrderResponse>>> getAllOrders(
            @PageableDefault(
                    size = MAX_PAGE_SIZE,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        Pageable limitedPageable = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), MAX_PAGE_SIZE),
                pageable.getSort()
        );

        Page<AdminOrderResponse> response = orderService.getAllOrders(limitedPageable);

        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "Orders fetched successfully",
                        response
                )
        );
    }
}
