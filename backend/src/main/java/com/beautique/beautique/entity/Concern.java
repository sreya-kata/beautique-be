package com.beautique.beautique.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "concerns", schema = "app")
public class Concern {
    @Id
    @Column(name = "concern_id")
    private Integer concernId;

    @Column(name = "concern_name")
    private String concernName;

    @Column(name = "category")
    private String category;
}