package com.beautique.beautique.service;

import com.beautique.beautique.dto.SkincareProfileResponse;
import com.beautique.beautique.entity.product.Product;
import com.beautique.beautique.model.Message;
import com.beautique.beautique.model.OpenAIRequest;
import com.beautique.beautique.model.OpenAIResponse;
import com.beautique.beautique.repository.product.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    private final ProfileService profileService;
    private final ProductRepository productRepository;
    private final OpenAIService openAIService;

    public RecommendationService(ProfileService profileService, ProductRepository productRepository, OpenAIService openAIService) {
        this.profileService = profileService;
        this.productRepository = productRepository;
        this.openAIService = openAIService;
    }

    public List<Product> getRecommendations(Integer userId) throws JsonProcessingException {
        // Get the full skincare profile
        SkincareProfileResponse profile = profileService.getSkincareProfile(userId);
        List<String> userConcerns = profile.getConcerns();

        // Fetch relevant products from the database
        List<Product> products = productRepository.findProductsByProfile(
                profile.getPrefersVegan(), profile.getPrefersCrueltyFree(), profile.getPrefersClean(),
                profile.getBudget(), userConcerns);

        // Format product details for AI
        String productDetails = products.stream()
                .map(p -> p.getProductId() + " - " + p.getName() + " - " + p.getBrand() + " ($" + p.getPrice() + ")")
                .collect(Collectors.joining("\n"));

        // AI prompt
        String prompt = "A user with " + profile.getSkinType() + " skin, concerns: " +
                String.join(", ", userConcerns) +
                " is looking for skincare products. Recommend the best ones from:\n" + productDetails +
                "\nReturn recommendations in JSON format:\n" +
                "{ \"recommended_products\": [ { \"product_id\": \"<product_id>\" } ] }";

        OpenAIRequest request = new OpenAIRequest();
        request.setModel("gpt-3.5-turbo");
        request.setMessages(List.of(new Message("user", prompt)));

        OpenAIResponse response = openAIService.sendRequest(request);

        // Send request to OpenAI
        String jsonResponse = response.getChoices().get(0).getMessage().getContent();

        // Parse JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        List<JsonNode> recommendations = rootNode.get("recommended_products").findValues("product_id");

        // Map product IDs to actual products
        List<String> recommendedProductIds = recommendations.stream()
                .map(JsonNode::asText)
                .collect(Collectors.toList());

        return products.stream().filter(p -> recommendedProductIds.contains(p.getProductId()))
                .collect(Collectors.toList());
    }
}
