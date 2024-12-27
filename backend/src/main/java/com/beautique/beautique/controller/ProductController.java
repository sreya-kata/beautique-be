package com.beautique.beautique.controller;

import com.beautique.beautique.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/fetch")
    public ResponseEntity<String> fetchAndStoreProducts(@RequestParam String categoryId,
                                                        @RequestParam Integer currentPage,
                                                        @RequestParam String category) {
        productService.fetchAndStoreProducts(categoryId, currentPage, category);
        return ResponseEntity.ok("Products fetched and stored successfully.");
    }
}

