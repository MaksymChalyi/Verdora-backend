package com.verdorabackend.repository;

import com.verdorabackend.entity.Order;
import com.verdorabackend.entity.OrderStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public final class OrderSpecification {

    private OrderSpecification() {
    }

    public static Specification<Order> filter(
            OrderStatus status,
            LocalDate dateFrom,
            LocalDate dateTo
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (dateFrom != null) {
                OffsetDateTime from = dateFrom.atStartOfDay().atOffset(ZoneOffset.UTC);
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.<OffsetDateTime>get("createdAt"),
                                from
                        )
                );
            }

            if (dateTo != null) {
                OffsetDateTime toExclusive = dateTo.plusDays(1)
                        .atStartOfDay()
                        .atOffset(ZoneOffset.UTC);

                predicates.add(
                        cb.lessThan(
                                root.<OffsetDateTime>get("createdAt"),
                                toExclusive
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
