package com.beautique.beautique.repository.product;

import com.beautique.beautique.entity.product.ProductIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductIngredientRepository extends JpaRepository<ProductIngredient, Integer> {
}
