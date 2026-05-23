package com.verdorabackend.service;

import com.verdorabackend.dto.response.FavoriteResponse;

import java.util.List;

public interface FavoriteService {

    List<FavoriteResponse> getFavorites(Long userId);

    boolean isFavorite(Long userId, Long productId);

    FavoriteResponse addFavorite(Long userId, Long productId);

    void removeFavorite(Long userId, Long productId);
}