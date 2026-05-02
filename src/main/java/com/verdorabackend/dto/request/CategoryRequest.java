package com.verdorabackend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Category request payload")
public record CategoryRequest(
        @Schema(description = "Category name", example = "Electronics") @NotBlank @Size(max = 256)
                String name) {}
