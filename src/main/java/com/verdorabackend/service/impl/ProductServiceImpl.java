package com.verdorabackend.service.impl;

import com.verdorabackend.dto.request.ProductRequest;
import com.verdorabackend.dto.response.ProductResponse;
import com.verdorabackend.entity.Category;
import com.verdorabackend.entity.Product;
import com.verdorabackend.exception.CategoryNotFoundException;
import com.verdorabackend.exception.ProductNotFoundException;
import com.verdorabackend.mapper.ProductMapper;
import com.verdorabackend.repository.CategoryRepository;
import com.verdorabackend.repository.ProductRepository;
import com.verdorabackend.repository.ProductSpecification;
import com.verdorabackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean discount,
            String search,
            Pageable pageable
    ) {
        log.debug("Fetching products: categoryId={}, minPrice={}, maxPrice={}, discount={}, search={}",
                categoryId, minPrice, maxPrice, discount, search);

        Specification<Product> spec = ProductSpecification.filter(
                categoryId, minPrice, maxPrice, discount, search);

        return productRepository.findAll(spec, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long productId) {
        log.debug("Fetching product id={}", productId);
        return productMapper.toResponse(getByIdOrThrow(productId));
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        log.debug("Creating product with name: {}", productRequest.name());

        Category category = categoryRepository.findById(productRequest.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(productRequest.categoryId()));

        Product product = productMapper.toEntity(productRequest);
        product.setCategory(category);
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
        Category category = categoryRepository.findById(productRequest.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(productRequest.categoryId()));
        product.setCategory(category);
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
