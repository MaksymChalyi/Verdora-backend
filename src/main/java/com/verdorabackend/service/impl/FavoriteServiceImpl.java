package com.verdorabackend.service.impl;

import com.verdorabackend.dto.response.FavoriteResponse;
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
import com.verdorabackend.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final FavoriteMapper favoriteMapper;

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteResponse> getFavorites(Long userId) {
        log.debug("Fetching favorites for userId={}", userId);
        return favoriteRepository.findByUser_Id(userId)
                .stream()
                .map(favoriteMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public FavoriteResponse addFavorite(Long userId, Long productId) {
        log.debug("Adding productId={} to favorites for userId={}", productId, userId);

        FavoriteId favoriteId = new FavoriteId(userId, productId);

        if (favoriteRepository.existsById(favoriteId)) {
            throw new FavoriteAlreadyExistsException(productId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        Favorite favorite = new Favorite();
        favorite.setId(favoriteId);
        favorite.setUser(user);
        favorite.setProduct(product);

        Favorite saved = favoriteRepository.save(favorite);
        log.info("Added productId={} to favorites for userId={}", productId, userId);
        return favoriteMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long productId) {
        log.debug("Removing productId={} from favorites for userId={}", productId, userId);

        FavoriteId favoriteId = new FavoriteId(userId, productId);

        if (!favoriteRepository.existsById(favoriteId)) {
            throw new FavoriteNotFoundException(productId);
        }

        favoriteRepository.deleteById(favoriteId);
        log.info("Removed productId={} from favorites for userId={}", productId, userId);
    }

}
