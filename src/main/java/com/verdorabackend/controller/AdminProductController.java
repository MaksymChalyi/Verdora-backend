package com.verdorabackend.controller;

import com.verdorabackend.dto.response.BaseResponse;
import com.verdorabackend.dto.response.BaseResponseFactory;
import com.verdorabackend.dto.response.ProductResponse;
import com.verdorabackend.service.ProductService;
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
@RequestMapping("/admin/products")
public class AdminProductController {

    private static final int MAX_PAGE_SIZE = 12;

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<ProductResponse>>> getAllProducts(
            @PageableDefault(
                    size = MAX_PAGE_SIZE,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        Pageable limitedPageable = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), MAX_PAGE_SIZE),
                pageable.getSort()
        );

        Page<ProductResponse> response = productService.getProducts(
                null,
                null,
                null,
                null,
                null,
                limitedPageable
        );

        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "Products fetched successfully",
                        response
                )
        );
    }
}
