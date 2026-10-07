
package com.verdorabackend.service.impl;

import com.verdorabackend.dto.response.FavoriteResponse;
import com.verdorabackend.dto.response.FavoriteStateResponse;
import com.verdorabackend.entity.Favorite;
import com.verdorabackend.entity.FavoriteId;
import com.verdorabackend.entity.Product;
import com.verdorabackend.entity.User;
import com.verdorabackend.exception.FavoriteAlreadyExistsException;
import com.verdorabackend.exception.FavoriteNotFoundException;
import com.verdorabackend.exception.ProductNotFoundException;
import com.verdorabackend.exception.UserNotFoundException;
import com.verdorabackend.mapper.FavoriteMapper;
import com.verdorabackend.repository.FavoriteRepository;
import com.verdorabackend.repository.ProductRepository;
import com.verdorabackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FavoriteMapper favoriteMapper;

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

    // ── GET FAVORITES ─────────────────────────────────────────────

    @Test
    void getFavorites_returnsList() {
        FavoriteResponse response = mockFavoriteResponse();
        Pageable pageable = PageRequest.of(0, 12);
        when(favoriteRepository.findByUser_Id(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(favorite), pageable, 1));
        when(favoriteMapper.toResponse(favorite)).thenReturn(response);

        Page<FavoriteResponse> result = favoriteService.getFavorites(1L, pageable);

        assertThat(result.getContent()).containsExactly(response);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(favoriteRepository).findByUser_Id(1L, pageable);
        verify(favoriteMapper).toResponse(favorite);
    }

    @Test
    void getFavorites_empty_returnsEmptyList() {
        Pageable pageable = PageRequest.of(0, 12);
        when(favoriteRepository.findByUser_Id(1L, pageable))
                .thenReturn(Page.empty(pageable));

        Page<FavoriteResponse> result = favoriteService.getFavorites(1L, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        verifyNoInteractions(favoriteMapper);
    }

    // ── IS FAVORITE ──────────────────────────────────────────────

    @Test
    void isFavorite_exists_returnsTrue() {
        when(favoriteRepository.existsById(favoriteId)).thenReturn(true);

        boolean result = favoriteService.isFavorite(1L, 1L);

        assertThat(result).isTrue();
        verify(favoriteRepository).existsById(favoriteId);
    }

    @Test
    void isFavorite_notExists_returnsFalse() {
        FavoriteId missingId = new FavoriteId(1L, 99L);
        when(favoriteRepository.existsById(missingId)).thenReturn(false);

        boolean result = favoriteService.isFavorite(1L, 99L);

        assertThat(result).isFalse();
        verify(favoriteRepository).existsById(missingId);
    }

    // ── ADD FAVORITE ─────────────────────────────────────────────

    @Test
    void addFavorite_newFavorite_addsSuccessfully() {
        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
        when(favoriteRepository.existsById(favoriteId)).thenReturn(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        FavoriteStateResponse result = favoriteService.addFavorite(1L, 1L);

        assertThat(result.productId()).isEqualTo(1L);
        assertThat(result.favorite()).isTrue();

        InOrder inOrder = inOrder(
                userRepository,
                favoriteRepository,
                productRepository
        );

        inOrder.verify(userRepository).findByIdForUpdate(1L);
        inOrder.verify(favoriteRepository).existsById(favoriteId);
        inOrder.verify(productRepository).findById(1L);
        inOrder.verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void addFavorite_alreadyExists_throwsException() {
        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
        when(favoriteRepository.existsById(favoriteId))
                .thenReturn(true);

        assertThatThrownBy(() -> favoriteService.addFavorite(1L, 1L))
                .isInstanceOf(FavoriteAlreadyExistsException.class);

        InOrder inOrder = inOrder(userRepository, favoriteRepository);

        inOrder.verify(userRepository).findByIdForUpdate(1L);
        inOrder.verify(favoriteRepository).existsById(favoriteId);

        verify(favoriteRepository, never()).save(any());
        verifyNoInteractions(productRepository);
    }

    @Test
    void addFavorite_productNotFound_throwsException() {
        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
        when(favoriteRepository.existsById(favoriteId)).thenReturn(false);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.addFavorite(1L, 1L))
                .isInstanceOf(ProductNotFoundException.class);
        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void addFavorite_userNotFound_throwsException() {
        when(userRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.addFavorite(1L, 1L))
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(favoriteRepository, productRepository);
    }

    // ── REMOVE FAVORITE ──────────────────────────────────────────

    @Test
    void removeFavorite_exists_removesSuccessfully() {
        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
        when(favoriteRepository.existsById(favoriteId)).thenReturn(true);

        FavoriteStateResponse result = favoriteService.removeFavorite(1L, 1L);

        assertThat(result.productId()).isEqualTo(1L);
        assertThat(result.favorite()).isFalse();

        InOrder inOrder = inOrder(userRepository, favoriteRepository);

        inOrder.verify(userRepository).findByIdForUpdate(1L);
        inOrder.verify(favoriteRepository).existsById(favoriteId);
        inOrder.verify(favoriteRepository).deleteById(favoriteId);
    }

    @Test
    void removeFavorite_notExists_throwsException() {
        FavoriteId missingId = new FavoriteId(1L, 99L);

        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
        when(favoriteRepository.existsById(missingId)).thenReturn(false);

        assertThatThrownBy(() -> favoriteService.removeFavorite(1L, 99L))
                .isInstanceOf(FavoriteNotFoundException.class);

        verify(favoriteRepository, never()).deleteById(any());
    }

    @Test
    void removeFavorite_userNotFound_throwsException() {
        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.removeFavorite(1L, 1L))
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(favoriteRepository);
    }

    // ── HELPERS ──────────────────────────────────────────────────

    private FavoriteResponse mockFavoriteResponse() {
        return new FavoriteResponse(
                1L, "Laptop", "https://img.url",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(800),
                OffsetDateTime.now()
        );
    }
}
