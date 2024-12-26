package com.beautique.beautique.service;

import com.beautique.beautique.dto.SephoraProductListResponse;
import com.beautique.beautique.enums.Category;
import com.beautique.beautique.enums.Source;
import com.beautique.beautique.entity.Product;
import com.beautique.beautique.repository.IngredientRepository;
import com.beautique.beautique.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import java.time.LocalDateTime;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final SephoraApiService sephoraApiService;

    public ProductService(ProductRepository productRepository,
                          SephoraApiService sephoraApiService) {
        this.productRepository = productRepository;
        this.sephoraApiService = sephoraApiService;
    }

    @Transactional
    public void fetchAndStoreProducts(String categoryId, Integer currentPage) {
        List<SephoraProductListResponse> productList = sephoraApiService.fetchProductList(categoryId, currentPage);
        List<Product> products = productList.stream()
                .map(this::mapToProduct)
                .collect(Collectors.toList());
        productRepository.saveAll(products);

        // Step 3: Fetch details for additional fields (ingredientDesc, shortDescription)
        productList.forEach(productSummary -> {
            SephoraProductDetailsResponse details = sephoraApiService.fetchProductDetails(productSummary.getProductUrl());

            // Extract and save ingredients
            List<String> ingredients = parseIngredients(details.getCurrentSku().getIngredientDesc());
            saveIngredientsBatch(ingredients, productSummary.getProductId());

            // Extract and save skincare concerns
            List<String> concerns = parseSkincareConcerns(details.getShortDescription());
            saveSkincareConcernsBatch(concerns, productSummary.getProductId());
        });
    }

    private Product mapToProduct(SephoraProductListResponse productSummary) {
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
        return product;
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
//
//    private List<String> parseIngredients(String ingredientDesc) {
//        if (ingredientDesc == null || ingredientDesc.isEmpty()) {
//            return Collections.emptyList();
//        }
//        return Arrays.stream(ingredientDesc.split("<br>|,"))
//                .map(String::trim)
//                .filter(ingredient -> !ingredient.contains(":") && !ingredient.isEmpty())
//                .collect(Collectors.toList());
//    }

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

//    private void saveIngredientsBatch(List<String> ingredients, String productId) {
//        List<Ingredient> ingredientEntities = ingredients.stream()
//                .map(name -> ingredientRepository.findByName(name)
//                        .orElseGet(() -> new Ingredient(name)))
//                .collect(Collectors.toList());
//        ingredientRepository.saveAll(ingredientEntities);
//
//        // Link ingredients to the product (if needed)
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

