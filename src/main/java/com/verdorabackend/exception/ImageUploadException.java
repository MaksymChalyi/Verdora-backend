package com.verdorabackend.exception;

import org.springframework.http.HttpStatus;

public class ImageUploadException extends BaseException {

    public ImageUploadException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image");
    }
}
