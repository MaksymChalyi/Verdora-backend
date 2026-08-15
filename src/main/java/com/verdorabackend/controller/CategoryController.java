package com.verdorabackend.controller;

import com.verdorabackend.dto.request.CategoryRequest;
import com.verdorabackend.dto.response.BaseResponse;
import com.verdorabackend.dto.response.BaseResponseFactory;
import com.verdorabackend.dto.response.CategoryResponse;
import com.verdorabackend.service.CategoryService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Category Management", description = "Endpoints for managing product categories")
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all categories", description = "Returns all categories")
    @ApiResponse(responseCode = "200", description = "Categories returned")
    @GetMapping
    public ResponseEntity<BaseResponse<List<CategoryResponse>>> getAllCategories() {
        log.info("Request to get all categories");
        List<CategoryResponse> response = categoryService.getAllCategories();
        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Categories fetched successfully", response)
        );
    }

    @Operation(summary = "Get category by ID", description = "Returns a single category by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category returned"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CategoryResponse>> getCategory(
            @PathVariable Long id) {
        log.info("Request to get category id={}", id);
        CategoryResponse response = categoryService.getCategory(id);
        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Category fetched successfully", response)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Cookie-based Authentication")
    @Operation(summary = "Create category", description = "Creates a new product category")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Category created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-05-23T12:00:00Z",
                                      "status": 201,
                                      "message": "Category created",
                                      "data": {
                                        "categoryId": 1,
                                        "name": "Electronics"
                                      }
                                    }
                                    """)
                    )
            )
    })
    @SecurityRequirement(name = "Cookie-based Authentication")
    @PostMapping
    public ResponseEntity<BaseResponse<CategoryResponse>> createCategory(
            @RequestBody @Valid CategoryRequest request) {
        log.info("Request to create category: {}", request.name());
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponseFactory.success(HttpStatus.CREATED, "Category created", response)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Cookie-based Authentication")
    @Operation(summary = "Update category", description = "Updates existing category by ID")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Category updated", response)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Cookie-based Authentication")
    @Operation(summary = "Delete category", description = "Deletes category by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteCategory(@PathVariable Long id) {
        log.info("Request to delete category id={}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(
                BaseResponseFactory.success(HttpStatus.OK, "Category deleted successfully")
        );
    }
}
