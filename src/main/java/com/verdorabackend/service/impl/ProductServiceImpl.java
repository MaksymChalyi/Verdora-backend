package com.verdorabackend.service.impl;

import com.verdorabackend.dto.request.ProductRequest;
import com.verdorabackend.dto.response.ProductResponse;
import com.verdorabackend.entity.Product;
import com.verdorabackend.exception.ProductNotFoundException;
import com.verdorabackend.mapper.ProductMapper;
import com.verdorabackend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        log.debug("Creating product with name: {}", productRequest.name());
        Product product = productMapper.toEntity(productRequest);
        Product saved = productRepository.save(product);
        log.info("Product created, id={}", saved.getId());
        return productMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest productRequest) {
        log.debug("Updating product with id: {}", productId);

        Product product = getByIdOrThrow(productId);

        productMapper.updateProductFromRequest(productRequest, product);

        Product updatedProduct = productRepository.save(product);

        log.info("Product updated with id: {}", updatedProduct.getId());

        return productMapper.toResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        log.debug("Deleting product with id: {}", productId);
        Product product = getByIdOrThrow(productId);
        productRepository.delete(product);
        log.info("Product deleted, id={}", productId);
    }

    private Product getByIdOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
