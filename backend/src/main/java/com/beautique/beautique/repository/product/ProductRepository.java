package com.beautique.beautique.repository.product;

import com.beautique.beautique.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findByProductId(String productId);

    @Query("SELECT DISTINCT p FROM Product p " +
            "JOIN ProductConcern pc ON p.productId = pc.productId " +
            "JOIN Concern c ON pc.concernId = c.concernId " +
            "WHERE (:prefersVegan = TRUE AND p.isVegan = TRUE OR :prefersVegan = FALSE) " +
            "AND (:prefersCrueltyFree = TRUE AND p.isCrueltyFree = TRUE OR :prefersCrueltyFree = FALSE) " +
            "AND (:prefersClean = TRUE AND p.isCleanAtSephora = TRUE OR :prefersClean = FALSE) " +
            "AND (p.price <= :budget) " +
            "AND c.concernName IN :concerns " +
            "ORDER BY p.rating DESC")
    List<Product> findProductsByProfile(
            @Param("prefersVegan") Boolean prefersVegan,
            @Param("prefersCrueltyFree") Boolean prefersCrueltyFree,
            @Param("prefersClean") Boolean prefersClean,
            @Param("budget") Integer budget,
            @Param("concerns") List<String> concerns
    );

}
