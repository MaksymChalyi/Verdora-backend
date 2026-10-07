package com.verdorabackend.service.impl;

import com.verdorabackend.dto.request.ProductRequest;
import com.verdorabackend.dto.response.ProductResponse;
import com.verdorabackend.entity.Category;
import com.verdorabackend.entity.Product;
import com.verdorabackend.exception.CategoryNotFoundException;
import com.verdorabackend.exception.ProductDeletionException;
import com.verdorabackend.exception.ProductNotFoundException;
import com.verdorabackend.mapper.ProductMapper;
import com.verdorabackend.repository.CategoryRepository;
import com.verdorabackend.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void getProducts_returnsMappedProducts() {
        Product product = mock(Product.class);
        ProductResponse response = mock(ProductResponse.class);
        Pageable pageable = mock(Pageable.class);

        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findAll(
                ArgumentMatchers.<Specification<Product>>any(),
                eq(pageable)
        )).thenReturn(productPage);

        when(productMapper.toResponse(product))
                .thenReturn(response);

        Page<ProductResponse> result = productService.getProducts(
                null,
                null,
                null,
                null,
                null,
                pageable
        );

        assertEquals(1, result.getTotalElements());
        assertSame(response, result.getContent().getFirst());

        verify(productMapper).toResponse(product);
    }

    @Test
    void getProduct_existingProduct_returnsResponse() {
        Product product = mock(Product.class);
        ProductResponse response = mock(ProductResponse.class);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productMapper.toResponse(product))
                .thenReturn(response);

        ProductResponse result = productService.getProduct(1L);

        assertSame(response, result);

        verify(productRepository).findById(1L);
        verify(productMapper).toResponse(product);
    }

    @Test
    void getProduct_notFound_throwsProductNotFoundException() {
        when(productRepository.findById(99999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProduct(99999L)
        );

        verify(productRepository).findById(99999L);
        verifyNoInteractions(productMapper);
    }

    @Test
    void createProduct_validRequest_returnsResponse() {
        ProductRequest request = mock(ProductRequest.class);
        Category category = mock(Category.class);
        Product product = mock(Product.class);
        Product savedProduct = mock(Product.class);
        ProductResponse response = mock(ProductResponse.class);

        when(request.categoryId()).thenReturn(1L);
        when(request.name()).thenReturn("Test Product");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productMapper.toEntity(request))
                .thenReturn(product);

        when(productRepository.save(product))
                .thenReturn(savedProduct);

        when(productMapper.toResponse(savedProduct))
                .thenReturn(response);

        ProductResponse result = productService.createProduct(request);

        assertSame(response, result);

        verify(product).setCategory(category);
        verify(productRepository).save(product);
        verify(productMapper).toResponse(savedProduct);
    }

    @Test
    void createProduct_categoryNotFound_throwsCategoryNotFoundException() {
        ProductRequest request = mock(ProductRequest.class);

        when(request.categoryId()).thenReturn(99999L);
        when(request.name()).thenReturn("Test Product");

        when(categoryRepository.findById(99999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> productService.createProduct(request)
        );

        verify(categoryRepository).findById(99999L);
        verify(productRepository, never()).save(any());
        verify(productMapper, never()).toEntity(any());
    }

    @Test
    void updateProduct_validRequest_returnsUpdatedResponse() {
        ProductRequest request = mock(ProductRequest.class);
        Product product = mock(Product.class);
        Category category = mock(Category.class);
        Product savedProduct = mock(Product.class);
        ProductResponse response = mock(ProductResponse.class);

        when(request.categoryId()).thenReturn(1L);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(product))
                .thenReturn(savedProduct);

        when(productMapper.toResponse(savedProduct))
                .thenReturn(response);

        ProductResponse result = productService.updateProduct(1L, request);

        assertSame(response, result);

        verify(productMapper)
                .updateProductFromRequest(request, product);

        verify(product)
                .setCategory(category);

        verify(productRepository)
                .save(product);

        verify(productMapper)
                .toResponse(savedProduct);
    }

    @Test
    void updateProduct_productNotFound_throwsProductNotFoundException() {
        ProductRequest request = mock(ProductRequest.class);

        when(productRepository.findById(99999L))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(99999L, request));
        verify(productRepository).findById(99999L);
        verify(productRepository, never()).save(any());
        verifyNoInteractions(productMapper, categoryRepository);
    }

    @Test
    void updateProduct_categoryNotFound_throwsCategoryNotFoundException() {
        ProductRequest request = mock(ProductRequest.class);
        Product product = mock(Product.class);

        when(request.categoryId()).thenReturn(99999L);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(categoryRepository.findById(99999L))
                .thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> productService.updateProduct(1L, request));

        verify(productMapper).updateProductFromRequest(request, product);
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_existingProduct_deletesProduct() {
        Product product = mock(Product.class);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.deleteProduct(1L);
        verify(productRepository).delete(product);
        verify(productRepository).flush();
    }

    @Test
    void deleteProduct_notFound_throwsProductNotFoundException() {
        when(productRepository.findById(99999L))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(99999L));

        verify(productRepository, never()).delete(any(Product.class));
        verify(productRepository, never()).flush();
    }

    @Test
    void deleteProduct_whenDeletionBlocked_throwsProductDeletionException() {
        Product product = mock(Product.class);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        doThrow(new DataIntegrityViolationException("constraint violation"))
                .when(productRepository)
                .flush();

        ProductDeletionException exception = assertThrows(ProductDeletionException.class, () -> productService.deleteProduct(1L));

        assertEquals("Product cannot be deleted because it is currently in use, id=1", exception.getMessage());
        verify(productRepository).delete(product);
        verify(productRepository).flush();
    }

    @Test
    void getProductOfTheDay_highestDiscountPercentage_returnsProduct() {
        Product first = product(
                1L,
                "1000.00",
                "700.00"
        );

        Product second = product(
                2L,
                "100.00",
                "50.00"
        );

        ProductResponse response = mock(ProductResponse.class);

        when(productRepository.findDiscountedProducts()).thenReturn(List.of(first, second));

        when(productMapper.toResponse(second)).thenReturn(response);

        Optional<ProductResponse> result = productService.getProductOfTheDay();

        assertTrue(result.isPresent());
        assertSame(response, result.orElseThrow());

        verify(productMapper).toResponse(second);
    }

    @Test
    void getProductOfTheDay_equalDiscount_usesLowestProductId() {
        Product higherId = product(
                2L,
                "200.00",
                "160.00"
        );

        Product lowerId = product(
                1L,
                "100.00",
                "80.00"
        );

        ProductResponse response = mock(ProductResponse.class);
        when(productRepository.findDiscountedProducts()).thenReturn(List.of(higherId, lowerId));
        when(productMapper.toResponse(lowerId)).thenReturn(response);

        Optional<ProductResponse> result = productService.getProductOfTheDay();

        assertTrue(result.isPresent());
        assertSame(response, result.orElseThrow());

        verify(productMapper).toResponse(lowerId);
    }

    @Test
    void getProductOfTheDay_noDiscountedProducts_returnsEmpty() {
        when(productRepository.findDiscountedProducts()).thenReturn(List.of());

        Optional<ProductResponse> result = productService.getProductOfTheDay();

        assertTrue(result.isEmpty());
        verifyNoInteractions(productMapper);
    }

    private Product product(Long id, String price, String discountPrice) {
        Product product = new Product();

        product.setId(id);
        product.setPrice(new BigDecimal(price));
        product.setDiscountPrice(new BigDecimal(discountPrice));

        return product;
    }
}
