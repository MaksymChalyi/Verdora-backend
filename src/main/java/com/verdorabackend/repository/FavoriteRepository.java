package com.verdorabackend.repository;

import com.verdorabackend.entity.Favorite;
import com.verdorabackend.entity.FavoriteId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Set;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {

    Page<Favorite> findByUser_Id(Long userId, Pageable pageable);

    boolean existsById(FavoriteId id);

    @Query("""
            SELECT f.id.productId
            FROM Favorite f
            WHERE f.id.userId = :userId
              AND f.id.productId IN :productIds
            """)
    Set<Long> findFavoriteProductIds(@Param("userId") Long userId, @Param("productIds") Collection<Long> productIds);
}
