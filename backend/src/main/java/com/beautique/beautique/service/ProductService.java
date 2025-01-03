package com.beautique.beautique.service;

import com.beautique.beautique.dto.sephora.SephoraProductDetailsResponse;
import com.beautique.beautique.dto.sephora.SephoraProductListResponse;
import com.beautique.beautique.entity.Concern;
import com.beautique.beautique.entity.Ingredient;
import com.beautique.beautique.entity.product.ProductConcern;
import com.beautique.beautique.entity.product.ProductIngredient;
import com.beautique.beautique.enums.Category;
import com.beautique.beautique.enums.Source;
import com.beautique.beautique.entity.product.Product;
import com.beautique.beautique.repository.ConcernRepository;
import com.beautique.beautique.repository.IngredientRepository;
import com.beautique.beautique.repository.product.ProductConcernRepository;
import com.beautique.beautique.repository.product.ProductIngredientRepository;
import com.beautique.beautique.repository.product.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import java.time.LocalDateTime;

@Service
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final IngredientRepository ingredientRepository;
    private final ConcernRepository concernRepository;
    private final ProductIngredientRepository productIngredientRepository;
    private final ProductConcernRepository productConcernRepository;
    private final SephoraApiService sephoraApiService;

    public ProductService(ProductRepository productRepository,
                          IngredientRepository ingredientRepository,
                          ConcernRepository concernRepository,
                          ProductIngredientRepository productIngredientRepository,
                          ProductConcernRepository productConcernRepository,
                          SephoraApiService sephoraApiService) {
        this.productRepository = productRepository;
        this.ingredientRepository = ingredientRepository;
        this.concernRepository = concernRepository;
        this.productIngredientRepository = productIngredientRepository;
        this.productConcernRepository = productConcernRepository;
        this.sephoraApiService = sephoraApiService;
    }

    @Transactional
    public void fetchAndStoreProducts(String categoryId, Integer currentPage, String category) {
        try {
            List<SephoraProductListResponse> productList = sephoraApiService.fetchProductList(categoryId, currentPage);

            List<Product> products = productList.stream()
                    .map(this::mapToProduct)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            saveProductsWithErrorLogging(products, "initial products");

            // Fetch all existing ingredients and concerns
            List<Ingredient> allIngredients = ingredientRepository.findAll();
            List<Concern> allConcerns = concernRepository.findAllByCategory(String.valueOf(category.equals(String.valueOf(Category.SKINCARE))
                    ? Category.SKINCARE : Category.HAIRCARE));

            Map<String, Ingredient> ingredientMap = allIngredients.stream()
                    .collect(Collectors.toMap(i -> i.getName().toLowerCase(), i -> i, (i1, i2) -> i1));
            Map<String, Concern> concernMap = allConcerns.stream()
                    .collect(Collectors.toMap(c -> c.getConcernName().toLowerCase(), c -> c, (c1, c2) -> c1));

            List<ProductIngredient> allProductIngredients = Collections.synchronizedList(new ArrayList<>());
            List<ProductConcern> allProductConcerns = Collections.synchronizedList(new ArrayList<>());

            List<Product> updatedProducts = Collections.synchronizedList(new ArrayList<>());

//            SephoraProductDetailsResponse productDetails;
//            try {
//                ObjectMapper objectMapper = new ObjectMapper();
//                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//                ClassPathResource resource = new ClassPathResource("mocks/SephoraProductDetailsApiResponse.json");
//                productDetails = objectMapper.readValue(resource.getInputStream(), SephoraProductDetailsResponse.class);
//            } catch (IOException e) {
//                e.printStackTrace();
//                return;
//            }

            // Process each product
            products.forEach(product -> {
                SephoraProductDetailsResponse productDetails = sephoraApiService.fetchProductDetails(product.getProductId(), product.getSkuId());
                sephoraApiService.delayBetweenCalls();
                if (productDetails == null) return;
                Boolean isVegan = productDetails.getProductDetails().getLongDescription().contains("vegan");
                Boolean isCrueltyFree = productDetails.getProductDetails().getLongDescription().contains("cruelty-free");
                Boolean isCleanAtSephora = productDetails.getProductDetails().getLongDescription().contains("CLEAN at Sephora");
                String suggestedUsage = productDetails.getProductDetails().getSuggestedUsage();
                String type = productDetails.getParentCategory().getDisplayName();

                // Update the product fields
                product.setIsVegan(isVegan);
                product.setIsCrueltyFree(isCrueltyFree);
                product.setIsCleanAtSephora(isCleanAtSephora);
                product.setSuggestedUsage(suggestedUsage);
                product.setType(type);
                updatedProducts.add(product);

                List<String> productIngredients = parseIngredients(productDetails.getCurrentSku().getIngredientDesc());
                List<String> productConcerns = new ArrayList<>();
                if (category.equals(String.valueOf(Category.SKINCARE))) {
                    productConcerns = parseSkincareConcerns(productDetails.getProductDetails().getShortDescription());
                }

                productIngredients.forEach(ingredientName -> {
                    Ingredient ingredient = ingredientMap.get(ingredientName.toLowerCase());
                    if (ingredient != null) {
                        ProductIngredient pi = new ProductIngredient();
                        pi.setProductId(product.getProductId());
                        pi.setIngredientId(ingredient.getId());
                        allProductIngredients.add(pi);
                    }
                });

                productConcerns.forEach(concernName -> {
                    Concern concern = concernMap.get(concernName.toLowerCase());
                    if (concern != null) {
                        ProductConcern pc = new ProductConcern();
                        pc.setProductId(product.getProductId());
                        pc.setConcernId(concern.getConcernId());
                        allProductConcerns.add(pc);
                    }
                });
            });

            // Batch update all products
            saveProductsWithErrorLogging(updatedProducts, "updated products");

            // Batch insert product ingredients and concerns
            saveProductIngredientsWithErrorLogging(allProductIngredients);
            saveProductConcernsWithErrorLogging(allProductConcerns);
        }
        catch (Exception e) {
            logger.error("Error in fetchAndStoreProducts: ", e);
            throw new RuntimeException("Failed to fetch and store products", e);
        }
    }

    private void saveProductsWithErrorLogging(List<Product> products, String context) {
        try {
            productRepository.saveAll(products);
        } catch (DataAccessException e) {
            logger.error("Error saving " + context + ": ", e);
            throw new RuntimeException("Failed to save " + context, e);
        }
    }

    private void saveProductIngredientsWithErrorLogging(List<ProductIngredient> productIngredients) {
        try {
            productIngredientRepository.saveAll(productIngredients);
        } catch (DataAccessException e) {
            logger.error("Error saving product ingredients: ", e);
            throw new RuntimeException("Failed to save product ingredients", e);
        }
    }

    private void saveProductConcernsWithErrorLogging(List<ProductConcern> productConcerns) {
        try {
            productConcernRepository.saveAll(productConcerns);
        } catch (DataAccessException e) {
            logger.error("Error saving product concerns: ", e);
            throw new RuntimeException("Failed to save product concerns", e);
        }
    }

    private Optional<Product> mapToProduct(SephoraProductListResponse productSummary) {
        if (productSummary.getDisplayName().contains("Kit") || productSummary.getDisplayName().contains("Set")) {
            return Optional.empty();
        }
        Product product = new Product();
        product.setProductId(productSummary.getProductId());
        product.setSkuId(productSummary.getCurrentSku().getSkuId());
        product.setName(productSummary.getDisplayName());
        product.setBrand(productSummary.getBrandName());
        product.setCategory(String.valueOf(Category.SKINCARE));
        product.setSource(String.valueOf(Source.SEPHORA));
        product.setPrice(parsePrice(productSummary.getCurrentSku().getListPrice()));
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

    private Double parsePrice(String listPrice) {
        return Arrays.stream(listPrice.split(" - "))
                .findFirst()
                .map(price -> Double.valueOf(price.replaceAll("[^\\d.]", "")))
                .orElse(null);
    }

    private List<String> parseIngredients(String ingredientDesc) {
        if (ingredientDesc == null || ingredientDesc.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(ingredientDesc.split("<br>|,"))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(ingredient -> !ingredient.contains(":") && !ingredient.isEmpty() && !ingredient.contains("<"))
                .collect(Collectors.toList());
    }

    private List<String> parseSkincareConcerns(String shortDescription) {
        if (shortDescription == null || shortDescription.isEmpty()) {
            return Collections.emptyList();
        }
        Pattern concernPattern = Pattern.compile("<strong>Skincare Concerns:</strong>\\s*(.*?)</p>");
        Matcher matcher = concernPattern.matcher(shortDescription);
        if (matcher.find()) {
            String concernsText = matcher.group(1);
            return Arrays.stream(concernsText.split(",|and"))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .filter(concern -> !concern.isEmpty())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}

