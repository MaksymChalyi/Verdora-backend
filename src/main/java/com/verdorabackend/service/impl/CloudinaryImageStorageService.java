package com.verdorabackend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.verdorabackend.exception.ImageUploadException;
import com.verdorabackend.exception.InvalidImageException;
import com.verdorabackend.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryImageStorageService implements ImageStorageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final Cloudinary cloudinary;

    @Override
    public String uploadProductImage(MultipartFile file) {
        validateImage(file);

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "verdora/products",
                            "resource_type", "image",
                            "unique_filename", true,
                            "overwrite", false
                    )
            );

            Object secureUrl = uploadResult.get("secure_url");

            if (!(secureUrl instanceof String imageUrl) || imageUrl.isBlank()) {
                log.error("Cloudinary response does not contain secure_url");
                throw new ImageUploadException();
            }

            return imageUrl;
        } catch (IOException | RuntimeException exception) {
            log.error("Failed to upload product image", exception);

            throw new ImageUploadException();
        }
    }

    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidImageException("Image file is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidImageException("Image size must not exceed 5 MB");
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidImageException("Only JPEG, PNG and WEBP images are supported");
        }
    }
}
