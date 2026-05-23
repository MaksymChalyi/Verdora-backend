package com.verdorabackend.service;

import com.verdorabackend.dto.response.FavoriteResponse;

import java.util.List;

public interface FavoriteService {

    List<FavoriteResponse> getFavorites(Long userId);

    FavoriteResponse addFavorite(Long userId, Long productId);

    void removeFavorite(Long userId, Long productId);
}
