package com.verdorabackend.repository;

import com.verdorabackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("""
        SELECT p
        FROM Product p
        WHERE p.discountPrice IS NOT NULL
          AND p.discountPrice < p.price
          AND p.price > 0
        """)
    List<Product> findDiscountedProducts();
}
