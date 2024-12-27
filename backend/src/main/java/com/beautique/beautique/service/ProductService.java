package com.beautique.beautique.service;

import com.beautique.beautique.dto.SephoraApiResponse;
import com.beautique.beautique.dto.SephoraProductDetailsResponse;
import com.beautique.beautique.dto.SephoraProductListResponse;
import com.beautique.beautique.entity.Ingredient;
import com.beautique.beautique.entity.ProductIngredient;
import com.beautique.beautique.enums.Category;
import com.beautique.beautique.enums.Source;
import com.beautique.beautique.entity.Product;
import com.beautique.beautique.repository.IngredientRepository;
import com.beautique.beautique.repository.ProductIngredientRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.beautique.beautique.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.time.LocalDateTime;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final IngredientRepository ingredientRepository;
    private final ProductIngredientRepository productIngredientRepository;
    private final SephoraApiService sephoraApiService;

    public ProductService(ProductRepository productRepository,
                          IngredientRepository ingredientRepository,
                          ProductIngredientRepository productIngredientRepository,
                          SephoraApiService sephoraApiService) {
        this.productRepository = productRepository;
        this.ingredientRepository = ingredientRepository;
        this.productIngredientRepository = productIngredientRepository;
        this.sephoraApiService = sephoraApiService;
    }

    @Transactional
    public void fetchAndStoreProducts(String categoryId, Integer currentPage) {
        //List<SephoraProductListResponse> productList = sephoraApiService.fetchProductList(categoryId, currentPage);
        SephoraApiResponse apiResponse;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ClassPathResource resource = new ClassPathResource("mocks/SephoraApiResponse.json");
            apiResponse = objectMapper.readValue(resource.getInputStream(), SephoraApiResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        List<SephoraProductListResponse> productList = apiResponse.getProducts();
        List<Product> products = productList.stream()
                .map(this::mapToProduct)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
//        productRepository.saveAll(products);

        SephoraProductDetailsResponse productDetailsResponse;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ClassPathResource resource = new ClassPathResource("mocks/SephoraProductDetailsApiResponse.json");
            productDetailsResponse = objectMapper.readValue(resource.getInputStream(), SephoraProductDetailsResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Step 3: Fetch details for additional fields (ingredientDesc, shortDescription)
        productList.forEach(productSummary -> {
//            SephoraProductDetailsResponse productDetailsResponse = sephoraApiService.fetchProductDetails(productSummary.getProductId(), productSummary.getCurrentSku().getSkuId());

            // Extract and save ingredients
            List<String> productIngredients = parseIngredients(productDetailsResponse.getCurrentSku().getIngredientDesc());
            List<Ingredient> ingredients = ingredientRepository.findAllByNameIn(productIngredients);
//            saveIngredientsBatch(productIngredients, productSummary.getProductId());

//            // Extract and save skincare concerns
//            List<String> concerns = parseSkincareConcerns(productDetailsResponse.getProductDetails().getShortDescription());
//            saveSkincareConcernsBatch(concerns, productSummary.getProductId());
        });
    }

    private Optional<Product> mapToProduct(SephoraProductListResponse productSummary) {
        if (productSummary.getDisplayName().contains("Kit") || productSummary.getDisplayName().contains("Set")) {
            return Optional.empty();
        }
        Product product = new Product();
        product.setProductId(productSummary.getProductId());
        product.setName(productSummary.getDisplayName());
        product.setBrand(productSummary.getBrandName());
        product.setCategory(String.valueOf(Category.SKINCARE));
        product.setSource(String.valueOf(Source.SEPHORA));
        setPriceRange(product, productSummary.getCurrentSku().getListPrice());
        product.setRating(Double.valueOf(productSummary.getRating()));
        product.setUrl(productSummary.getTargetUrl());
        product.setImageUrl(productSummary.getHeroImage());
        product.setImageAltText(productSummary.getCurrentSku().getImageAltText());

        product.setIsVegan(null);
        product.setIsCrueltyFree(null);
        product.setIsCleanAtSephora(null);

        // Set the created_at timestamp
        product.setCreatedAt(LocalDateTime.now());
        return Optional.of(product);
    }

    private void setPriceRange(Product product, String listPrice) {
        if (listPrice.contains(" - ")) {
            String[] prices = listPrice.split(" - ");
            product.setPrice(parsePrice(prices[0]));
        } else {
            product.setPrice(parsePrice(listPrice));
        }
    }

    private Double parsePrice(String priceString) {
        return Double.valueOf(priceString.replaceAll("[^\\d.]", ""));
    }

    private List<String> parseIngredients(String ingredientDesc) {
        if (ingredientDesc == null || ingredientDesc.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(ingredientDesc.split("<br>|,"))
                .map(String::trim)
                .filter(ingredient -> !ingredient.contains(":") && !ingredient.isEmpty())
                .collect(Collectors.toList());
    }

//    private List<String> parseSkincareConcerns(String shortDescription) {
//        if (shortDescription == null || shortDescription.isEmpty()) {
//            return Collections.emptyList();
//        }
//        Pattern concernPattern = Pattern.compile("Skincare Concerns:\\s*(.*)<");
//        Matcher matcher = concernPattern.matcher(shortDescription);
//        if (matcher.find()) {
//            String concernsText = matcher.group(1);
//            return Arrays.stream(concernsText.split(",|and"))
//                    .map(String::trim)
//                    .filter(concern -> !concern.isEmpty())
//                    .collect(Collectors.toList());
//        }
//        return Collections.emptyList();
//    }

//    private void saveIngredientsBatch(List<Ingredient> ingredients, String productId) {
//        List<ProductIngredient> productIngredients = ingredients.stream()
//                .map(new ProductIngredient(name)))
//                .collect(Collectors.toList());
//        productIngredientRepository.saveAll(productIngredients);
//    }

//    private void saveSkincareConcernsBatch(List<String> concerns, String productId) {
//        List<SkincareConcern> concernEntities = concerns.stream()
//                .map(name -> skincareConcernRepository.findByName(name)
//                        .orElseGet(() -> new SkincareConcern(name)))
//                .collect(Collectors.toList());
//        skincareConcernRepository.saveAll(concernEntities);
//
//        // Link concerns to the product (if needed)
//    }
}

