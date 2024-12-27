package com.beautique.beautique.repository;

import com.beautique.beautique.entity.ProductConcern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductConcernRepository extends JpaRepository<ProductConcern, Integer> {
}
