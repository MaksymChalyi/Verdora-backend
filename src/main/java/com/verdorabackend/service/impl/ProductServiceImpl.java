package com.verdorabackend.service.impl;

import com.verdorabackend.dto.request.ProductRequest;
import com.verdorabackend.dto.response.ProductResponse;
import com.verdorabackend.entity.Category;
import com.verdorabackend.entity.FavoriteId;
import com.verdorabackend.entity.Product;
import com.verdorabackend.exception.CategoryNotFoundException;
import com.verdorabackend.exception.ProductDeletionException;
import com.verdorabackend.exception.ProductNotFoundException;
import com.verdorabackend.mapper.ProductMapper;
import com.verdorabackend.repository.CategoryRepository;
import com.verdorabackend.repository.FavoriteRepository;
import com.verdorabackend.repository.ProductRepository;
import com.verdorabackend.repository.specification.ProductSpecification;
import com.verdorabackend.security.UserPrincipal;
import com.verdorabackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final FavoriteRepository favoriteRepository;

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
        Specification<Product> spec = ProductSpecification.filter(categoryId, minPrice, maxPrice, discount, search);
        Page<Product> products = productRepository.findAll(spec, pageable);
        Long userId = currentUserId();

        if (userId == null || products.isEmpty()) {
            return products.map(product ->
                    withFavorite(productMapper.toResponse(product), false)
            );
        }

        Set<Long> productIds = products.getContent().stream()
                .map(Product::getId)
                .collect(Collectors.toSet());

        Set<Long> favoriteIds = favoriteRepository.findFavoriteProductIds(userId, productIds);

        return products.map(product ->
                withFavorite(
                        productMapper.toResponse(product),
                        favoriteIds.contains(product.getId())
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long productId) {
        Product product = getByIdOrThrow(productId);
        Long userId = currentUserId();
        boolean favorite = userId != null && favoriteRepository.existsById(new FavoriteId(userId, productId));
        return withFavorite(productMapper.toResponse(product), favorite);
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
        try {
            productRepository.delete(product);
            productRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            log.warn("Product deletion blocked, id={}", productId);
            throw new ProductDeletionException(productId);
        }
        log.info("Product deleted, id={}", productId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductResponse> getProductOfTheDay() {
        List<Product> discountedProducts = productRepository.findDiscountedProducts();

        if (discountedProducts.isEmpty()) {
            return Optional.empty();
        }

        Product selected = discountedProducts.getFirst();

        for (int i = 1; i < discountedProducts.size(); i++) {
            Product current = discountedProducts.get(i);

            int comparison = compareDiscountPercentage(current, selected);

            if (comparison > 0 || (comparison == 0 && current.getId().compareTo(selected.getId()) < 0)) {
                selected = current;
            }
        }

        return Optional.of(productMapper.toResponse(selected));
    }

    private int compareDiscountPercentage(Product first, Product second) {
        BigDecimal firstDiscount = first.getPrice()
                .subtract(first.getDiscountPrice())
                .multiply(second.getPrice());

        BigDecimal secondDiscount = second.getPrice()
                .subtract(second.getDiscountPrice())
                .multiply(first.getPrice());

        return firstDiscount.compareTo(secondDiscount);
    }

    private Product getByIdOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return null;
        }

        return principal.getUser().getId();
    }

    private ProductResponse withFavorite(ProductResponse response, boolean favorite) {
        return new ProductResponse(
                response.productId(),
                response.name(),
                response.description(),
                response.price(),
                response.categoryId(),
                response.imageUrl(),
                response.discountPrice(),
                response.createdAt(),
                response.updatedAt(),
                favorite
        );
    }

}
