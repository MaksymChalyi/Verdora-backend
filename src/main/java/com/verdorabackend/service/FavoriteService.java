package com.verdorabackend.service;

import com.verdorabackend.dto.response.FavoriteResponse;
import com.verdorabackend.dto.response.FavoriteStateResponse;

import java.util.List;

public interface FavoriteService {

    List<FavoriteResponse> getFavorites(Long userId);

    boolean isFavorite(Long userId, Long productId);

    FavoriteStateResponse addFavorite(Long userId, Long productId);

    FavoriteStateResponse removeFavorite(Long userId, Long productId);
}
