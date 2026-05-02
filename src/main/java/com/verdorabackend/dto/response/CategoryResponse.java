package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Category response payload")
public record CategoryResponse(
        @Schema(description = "Category ID", example = "1") String categoryId,
        @Schema(description = "Category name", example = "Electronics") String category) {}
