package com.verdorabackend.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    String uploadProductImage(MultipartFile file);
}
