package com.beautique.beautique.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "products", schema = "app")
public class Product {
    @Id
    @Column(name = "product_id")
    private String productId;

    @Transient
    private String skuId;

    @Column(name = "name")
    private String name;

    @Column(name = "brand")
    private String brand;

    @Column(name = "type")
    private String type;

    @Column(name = "url")
    private String url;

    @Column(name = "price")
    private Double price;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_alt_text")
    private String imageAltText;

    @Column(name = "source")
    private String source;

    @Column(name = "category")
    private String category;

    @Column(name = "is_vegan")
    private Boolean isVegan;

    @Column(name = "is_cruelty_free")
    private Boolean isCrueltyFree;

    @Column(name = "is_clean_at_sephora")
    private Boolean isCleanAtSephora;

    @Column(name = "suggested_usage", columnDefinition = "TEXT")
    private String suggestedUsage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

