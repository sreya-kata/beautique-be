package com.beautique.beautique.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "ingredients", schema = "app")
public class Ingredient {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "category")
    private String category;

    @Column(name = "benefit")
    private String benefit;

    @Column(name = "potential_issues")
    private String potentialIssues;
}

