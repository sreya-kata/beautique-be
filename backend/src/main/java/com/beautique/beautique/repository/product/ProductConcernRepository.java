package com.beautique.beautique.repository.product;

import com.beautique.beautique.entity.product.ProductConcern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductConcernRepository extends JpaRepository<ProductConcern, Integer> {
}
