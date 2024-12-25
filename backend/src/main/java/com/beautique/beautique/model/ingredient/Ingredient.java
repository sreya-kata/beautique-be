package com.beautique.beautique.model.ingredient;

import com.beautique.beautique.model.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Ingredient {
    @Id
    private Long id;
    private String name;
    private String benefit;
    private String potentialIssues;

    @ManyToMany(mappedBy = "ingredients")
    private List<Product> products = new ArrayList<>();
}


