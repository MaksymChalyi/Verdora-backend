package com.verdorabackend.repository;

import com.verdorabackend.entity.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {
}
