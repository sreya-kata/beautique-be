package com.beautique.beautique.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SkincareProfileResponse {
    private String skinType;
    private String skinTone;
    private Integer budget;
    private String allergiesSensitivities;
    private Boolean prefersVegan;
    private Boolean prefersCrueltyFree;
    private Boolean prefersClean;
    private List<String> concerns;
}