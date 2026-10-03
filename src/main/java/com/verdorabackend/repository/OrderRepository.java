package com.verdorabackend.repository;

import com.verdorabackend.entity.Order;
import com.verdorabackend.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Page<Order> findByUser_Id(Long userId, Pageable pageable);

    Optional<Order> findByIdAndUser_Id(Long orderId, Long userId);

    List<Order> findByStatusAndCreatedAtBeforeOrderByCreatedAtAsc(OrderStatus status, OffsetDateTime createdAt);

    List<Order> findByStatusInAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtAsc(Collection<OrderStatus> statuses, OffsetDateTime from, OffsetDateTime to);
}
