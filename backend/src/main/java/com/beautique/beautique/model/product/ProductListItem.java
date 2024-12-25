package com.beautique.beautique.model.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductListItem {
    private String id;
    private String name;
    private String brand;
    private BigDecimal price;
    private String category;
}
