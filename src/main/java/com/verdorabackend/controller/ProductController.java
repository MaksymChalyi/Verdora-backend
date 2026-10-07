package com.verdorabackend.controller;

import com.verdorabackend.dto.request.ProductRequest;
import com.verdorabackend.dto.response.BaseResponse;
import com.verdorabackend.dto.response.BaseResponseFactory;
import com.verdorabackend.dto.response.ImageUploadResponse;
import com.verdorabackend.dto.response.ProductResponse;
import com.verdorabackend.service.ImageStorageService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Management", description = "Endpoints for managing products")
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final ImageStorageService imageStorageService;

    @Operation(
            summary = "Get products",
            description = "Returns paginated list of products with optional filters, search and sorting"
    )
    @ApiResponse(responseCode = "200", description = "Products returned")
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

            @Parameter(description = "Search by product name (case-insensitive)")
            @RequestParam(required = false) String search,

            @PageableDefault(size = 12, sort = "id") Pageable pageable
    ) {
        log.info("Request to get products: categoryId={}, minPrice={}, maxPrice={}, discount={}, search={}",
                categoryId, minPrice, maxPrice, discount, search);

        Page<ProductResponse> response = productService.getProducts(
                categoryId, minPrice, maxPrice, discount, search, pageable);

        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Products fetched successfully", response)
        );
    }

    @Operation(
            summary = "Get product by ID",
            description = "Returns a single product by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product returned"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductResponse>> getProduct(
            @PathVariable Long id) {
        log.info("Request to get product id={}", id);

        ProductResponse response = productService.getProduct(id);

        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Product fetched successfully", response)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Cookie-based Authentication")
    @Operation(summary = "Create product", description = "Creates a new product")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-05-23T12:00:00Z",
                                      "status": 201,
                                      "message": "Product created",
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
                BaseResponseFactory.success(HttpStatus.CREATED, "Product created", response)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Cookie-based Authentication")
    @Operation(summary = "Update product", description = "Updates existing product by ID")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid ProductRequest request) {
        log.info("Request to update product id={}", id);

        ProductResponse response = productService.updateProduct(id, request);

        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Product updated", response)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Cookie-based Authentication")
    @Operation(summary = "Delete product", description = "Deletes product by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteProduct(
            @PathVariable Long id) {
        log.info("Request to delete product id={}", id);

        productService.deleteProduct(id);

        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Product deleted successfully")
        );
    }

    @Operation(
            summary = "Get product of the day",
            description = """
                    Returns the product with the highest discount percentage.
                    If multiple products have the same highest discount,
                    the product with the lowest ID is returned.
                    If no discounted products exist, the endpoint returns
                    HTTP 200 with empty data.
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product of the day returned successfully or no discounted product available"
    )
    @GetMapping("/product-of-the-day")
    public ResponseEntity<BaseResponse<ProductResponse>> getProductOfTheDay() {
        log.info("Request to get product of the day");

        ProductResponse response = productService.getProductOfTheDay()
                .orElse(null);

        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "Product of the day fetched successfully",
                        response
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Cookie-based Authentication")
    @Operation(summary = "Upload product image", description = "Uploads a product image and returns its URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid image"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Image upload failed")
    })
    @PostMapping(
            value = "/upload-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<BaseResponse<ImageUploadResponse>> uploadImage(
            @RequestPart("file") MultipartFile file) {

        log.info("Request to upload product image: name={}, size={}", file.getOriginalFilename(), file.getSize());
        String imageUrl = imageStorageService.uploadProductImage(file);
        return ResponseEntity.ok(
                BaseResponseFactory.success(
                        HttpStatus.OK,
                        "Image uploaded successfully",
                        new ImageUploadResponse(imageUrl)
                )
        );
    }

}
