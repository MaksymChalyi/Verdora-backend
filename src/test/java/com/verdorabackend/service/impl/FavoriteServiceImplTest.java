package com.verdorabackend.service.impl;

import com.verdorabackend.dto.response.FavoriteResponse;
import com.verdorabackend.entity.*;
import com.verdorabackend.exception.FavoriteAlreadyExistsException;
import com.verdorabackend.exception.FavoriteNotFoundException;
import com.verdorabackend.exception.ProductNotFoundException;
import com.verdorabackend.mapper.FavoriteMapper;
import com.verdorabackend.repository.FavoriteRepository;
import com.verdorabackend.repository.ProductRepository;
import com.verdorabackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    @Mock private FavoriteRepository favoriteRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private FavoriteMapper favoriteMapper;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    private User user;
    private Product product;
    private Favorite favorite;
    private FavoriteId favoriteId;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(BigDecimal.valueOf(1000));
        product.setDiscountPrice(BigDecimal.valueOf(800));
        product.setImageUrl("https://img.url");

        favoriteId = new FavoriteId(1L, 1L);

        favorite = new Favorite();
        favorite.setId(favoriteId);
        favorite.setUser(user);
        favorite.setProduct(product);
        favorite.setCreatedAt(OffsetDateTime.now());
    }

    // ── getFavorites ─────────────────────────────────────────────────────────

    @Test
    void getFavorites_returnsList() {
        FavoriteResponse response = mockFavoriteResponse();
        when(favoriteRepository.findByUser_Id(1L)).thenReturn(List.of(favorite));
        when(favoriteMapper.toResponse(favorite)).thenReturn(response);

        List<FavoriteResponse> result = favoriteService.getFavorites(1L);

        assertThat(result).hasSize(1);
        verify(favoriteRepository).findByUser_Id(1L);
    }

    @Test
    void getFavorites_empty_returnsEmptyList() {
        when(favoriteRepository.findByUser_Id(1L)).thenReturn(List.of());

        List<FavoriteResponse> result = favoriteService.getFavorites(1L);

        assertThat(result).isEmpty();
    }

    // ── isFavorite ───────────────────────────────────────────────────────────

    @Test
    void isFavorite_exists_returnsTrue() {
        when(favoriteRepository.existsById(favoriteId)).thenReturn(true);

        boolean result = favoriteService.isFavorite(1L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    void isFavorite_notExists_returnsFalse() {
        when(favoriteRepository.existsById(any())).thenReturn(false);

        boolean result = favoriteService.isFavorite(1L, 99L);

        assertThat(result).isFalse();
    }

    // ── addFavorite ──────────────────────────────────────────────────────────

    @Test
    void addFavorite_newFavorite_addsSuccessfully() {
        FavoriteResponse response = mockFavoriteResponse();

        when(favoriteRepository.existsById(favoriteId)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(favoriteRepository.save(any())).thenReturn(favorite);
        when(favoriteMapper.toResponse(favorite)).thenReturn(response);

        FavoriteResponse result = favoriteService.addFavorite(1L, 1L);

        assertThat(result).isNotNull();
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void addFavorite_alreadyExists_throwsException() {
        when(favoriteRepository.existsById(favoriteId)).thenReturn(true);

        assertThatThrownBy(() -> favoriteService.addFavorite(1L, 1L))
                .isInstanceOf(FavoriteAlreadyExistsException.class);

        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void addFavorite_productNotFound_throwsException() {
        when(favoriteRepository.existsById(favoriteId)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.addFavorite(1L, 1L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    // ── removeFavorite ───────────────────────────────────────────────────────

    @Test
    void removeFavorite_exists_removesSuccessfully() {
        when(favoriteRepository.existsById(favoriteId)).thenReturn(true);

        favoriteService.removeFavorite(1L, 1L);

        verify(favoriteRepository).deleteById(favoriteId);
    }

    @Test
    void removeFavorite_notExists_throwsException() {
        when(favoriteRepository.existsById(any())).thenReturn(false);

        assertThatThrownBy(() -> favoriteService.removeFavorite(1L, 99L))
                .isInstanceOf(FavoriteNotFoundException.class);

        verify(favoriteRepository, never()).deleteById(any());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private FavoriteResponse mockFavoriteResponse() {
        return new FavoriteResponse(
                1L, "Laptop", "https://img.url",
                BigDecimal.valueOf(1000), BigDecimal.valueOf(800),
                OffsetDateTime.now()
        );
    }
}
