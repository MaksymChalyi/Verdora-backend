package com.verdorabackend.service;

import com.verdorabackend.dto.response.FavoriteResponse;
import com.verdorabackend.dto.response.FavoriteStateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteService {

    Page<FavoriteResponse> getFavorites(Long userId, Pageable pageable);

    boolean isFavorite(Long userId, Long productId);

    FavoriteStateResponse addFavorite(Long userId, Long productId);

    FavoriteStateResponse removeFavorite(Long userId, Long productId);
}
