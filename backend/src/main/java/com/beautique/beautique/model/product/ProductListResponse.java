package com.beautique.beautique.model.product;

import lombok.Data;

import java.util.List;

@Data
public class ProductListResponse {
    private List<ProductListItem> products;
}
