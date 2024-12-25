package com.beautique.beautique.service;

import com.beautique.beautique.model.product.Product;
import com.beautique.beautique.model.product.ProductListItem;
import com.beautique.beautique.model.product.ProductListResponse;
import com.beautique.beautique.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final RestTemplate restTemplate;
    private final ProductRepository productRepository;

    @Value("${sephora.api.list-url}")
    private String listUrl;

    @Value("${sephora.api.detail-url}")
    private String detailUrl;

    public ProductService(RestTemplateBuilder restTemplateBuilder, ProductRepository productRepository) {
        this.restTemplate = restTemplateBuilder.build();
        this.productRepository = productRepository;
    }

//    public void fetchAndStoreProducts() {
//        // Step 1: Fetch products from /list
//        ResponseEntity<ProductListResponse> response = restTemplate.exchange(
//                listUrl, HttpMethod.GET, null, new ParameterizedTypeReference<>() {}
//        );
//        List<ProductListItem> products = response.getBody().getProducts();
//
//        // Step 2: Process each product
//        for (ProductListItem item : products) {
//            String productId = item.getId();
//            Optional<Product> existingProduct = productRepository.findByProductId(productId);
//
//            if (existingProduct.isPresent() && existingProduct.get().getLastUpdated().isAfter(LocalDateTime.now().minusDays(1))) {
//                continue; // Skip recently updated products
//            }
//
//            // Fetch details from /detail
//            String detailApiUrl = detailUrl.replace("{productId}", productId);
//            ResponseEntity<ProductDetailResponse> detailResponse = restTemplate.exchange(
//                    detailApiUrl, HttpMethod.GET, null, ProductDetailResponse.class
//            );
//            ProductDetail detail = detailResponse.getBody();
//
//            // Save or update the product
//            Product product = existingProduct.orElse(new Product());
//            product.setProductId(productId);
//            product.setName(item.getName());
//            product.setBrand(item.getBrand());
//            product.setPrice(item.getPrice());
//            product.setIngredients(detail.getIngredients());
//            product.setSkincareConcerns(detail.getSkincareConcerns());
//            product.setIsVegan(detail.isVegan());
//            product.setIsCrueltyFree(detail.isCrueltyFree());
//            product.setLastUpdated(LocalDateTime.now());
//
//            productRepository.save(product);
//        }
//    }
}

