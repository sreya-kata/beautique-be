package com.beautique.beautique.controller;

import com.beautique.beautique.dto.SkincareProfileResponse;
import com.beautique.beautique.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<SkincareProfileResponse> getRecommendations(@PathVariable Integer userId) {
        SkincareProfileResponse skincareProfileResponse = recommendationService.getRecommendations(userId);
        return ResponseEntity.ok(skincareProfileResponse);
    }

}
