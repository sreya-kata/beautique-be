package com.beautique.beautique.entity.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import lombok.Data;

@Entity
@Data
@Table(name = "product_ingredients", schema = "app")
public class ProductIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_ingredient_id")
    private Integer productIngredientId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "ingredient_id")
    private Integer ingredientId;
}

