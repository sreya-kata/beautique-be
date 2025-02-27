package com.beautique.beautique.controller;

import com.beautique.beautique.dto.SkincareProfileResponse;
import com.beautique.beautique.entity.product.Product;
import com.beautique.beautique.service.RecommendationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Product>> getRecommendations(@PathVariable Integer userId) throws JsonProcessingException {
        List<Product> recommendedProducts = recommendationService.getRecommendations(userId);
        return ResponseEntity.ok(recommendedProducts);
    }

}
