package com.verdorabackend.controller;

import com.verdorabackend.dto.request.ProductRequest;
import com.verdorabackend.dto.response.BaseResponse;
import com.verdorabackend.dto.response.BaseResponseFactory;
import com.verdorabackend.dto.response.ProductResponse;
import com.verdorabackend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Management", description = "Endpoints for managing products")
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Cookie-based Authentication")
    @Operation(
            summary = "Create product",
            description = "Creates a new product"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-04-26T12:00:00Z",
                                      "status": 201,
                                      "message": "Product created successfully",
                                      "data": {
                                        "productId": 1,
                                        "name": "Laptop",
                                        "price": 500
                                      }
                                    }
                                    """)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<BaseResponse<ProductResponse>> createProduct(
            @RequestBody @Valid ProductRequest request) {
        log.info("Request to create product: {}", request.name());

        ProductResponse response = productService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponseFactory.success(
                        HttpStatus.CREATED,
                        "Product created",
                        response
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Cookie-based Authentication")
    @Operation(
            summary = "Update product",
            description = "Updates existing product by ID"
    )
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid ProductRequest request) {
        log.info("Request to update product id={}", id);

        ProductResponse response = productService.updateProduct(id, request);

        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "Product updated",
                        response
                )
        );
    }


    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Cookie-based Authentication")
    @Operation(
            summary = "Delete product",
            description = "Deletes product by ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteProduct(
            @PathVariable Long id) {
        log.info("Request to delete product id={}", id);

        productService.deleteProduct(id);

        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "Product deleted successfully"
                )
        );
    }

    @Operation(
            summary = "Get products",
            description = "Returns paginated list of products with optional filters"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products returned")
    })
    @GetMapping
    public ResponseEntity<BaseResponse<Page<ProductResponse>>> getProducts(
            @Parameter(description = "Filter by category ID")
            @RequestParam(required = false) Long categoryId,

            @Parameter(description = "Minimum price")
            @RequestParam(required = false) BigDecimal minPrice,

            @Parameter(description = "Maximum price")
            @RequestParam(required = false) BigDecimal maxPrice,

            @Parameter(description = "Only products with discount (discountPrice < price)")
            @RequestParam(required = false) Boolean discount,

            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        log.info("Request to get products: categoryId={}, minPrice={}, maxPrice={}, discount={}",
                categoryId, minPrice, maxPrice, discount);

        Page<ProductResponse> response = productService.getProducts(
                categoryId, minPrice, maxPrice, discount, pageable);

        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Products fetched successfully", response)
        );
    }
}
