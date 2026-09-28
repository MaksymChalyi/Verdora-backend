package com.verdorabackend.repository;

import com.verdorabackend.dto.response.TopPurchasedProductResponse;
import com.verdorabackend.entity.OrderItem;
import com.verdorabackend.entity.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
            SELECT new com.verdorabackend.dto.response.TopPurchasedProductResponse(
                oi.product.id,
                oi.product.name,
                SUM(oi.quantity)
            )
            FROM OrderItem oi
            WHERE oi.order.status IN :statuses
            GROUP BY oi.product.id, oi.product.name
            ORDER BY SUM(oi.quantity) DESC, oi.product.id ASC
            """)
    List<TopPurchasedProductResponse> findTopPurchasedProducts(
            @Param("statuses") Collection<OrderStatus> statuses,
            Pageable pageable
    );
}
