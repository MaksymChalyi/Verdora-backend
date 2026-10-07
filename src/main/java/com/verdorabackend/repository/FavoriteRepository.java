package com.verdorabackend.repository;

import com.verdorabackend.entity.Favorite;
import com.verdorabackend.entity.FavoriteId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {

    Page<Favorite> findByUser_Id(Long userId, Pageable pageable);

    boolean existsById(FavoriteId id);
}
