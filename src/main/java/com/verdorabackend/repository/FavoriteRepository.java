package com.verdorabackend.repository;

import com.verdorabackend.entity.Favorite;
import com.verdorabackend.entity.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {

    List<Favorite> findByUser_Id(Long userId);

    boolean existsById(FavoriteId id);
}
