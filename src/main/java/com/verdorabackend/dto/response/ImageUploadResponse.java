package com.verdorabackend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Uploaded image response")
public record ImageUploadResponse(

        @Schema(
                description = "Public HTTPS URL of the uploaded image",
                example = "https://res.cloudinary.com/example/image/upload/product.jpg"
        )
        String imageUrl
) {
}
