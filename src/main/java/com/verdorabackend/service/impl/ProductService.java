package com.verdorabackend.service.impl;

import com.verdorabackend.dto.request.ProductRequest;
import com.verdorabackend.dto.response.ProductResponse;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest);

    ProductResponse updateProduct(Long productId, ProductRequest productRequest);

    void deleteProduct(Long productId);
}
