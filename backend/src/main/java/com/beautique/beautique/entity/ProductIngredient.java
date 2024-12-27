package com.beautique.beautique.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "product_ingredients", schema = "app")
public class ProductIngredient {
    @Id
    @Column(name = "product_ingredient_id")
    private Integer productIngredientId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "ingredient_id")
    private Integer ingredientId;
}

