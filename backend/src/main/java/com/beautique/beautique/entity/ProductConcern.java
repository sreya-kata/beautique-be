package com.beautique.beautique.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
@Table(name = "product_concerns", schema = "app",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "concern_id"}))
public class ProductConcern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_concern_id")
    private Integer productIngredientId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "concern_id")
    private Integer concernId;
}
