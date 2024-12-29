package com.beautique.beautique.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "skincare_profiles", schema = "app")
public class SkincareProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Integer profileId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "skin_type")
    private String skinType;

    @Column(name = "skin_tone")
    private String skinTone;

    @Column(name = "budget")
    private Integer budget;

    @Column(name = "allergies_sensitivities", columnDefinition = "TEXT")
    private String allergiesSensitivities;

    @Column(name = "prefers_vegan")
    private Boolean prefersVegan;

    @Column(name = "prefers_cruelty_free")
    private Boolean prefersCrueltyFree;

    @Column(name = "prefers_clean")
    private Boolean prefersClean;
}
