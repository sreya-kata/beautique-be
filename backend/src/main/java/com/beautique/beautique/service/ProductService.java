package com.beautique.beautique.service;

import com.beautique.beautique.dto.SephoraApiResponse;
import com.beautique.beautique.dto.SephoraProductDetailsResponse;
import com.beautique.beautique.dto.SephoraProductListResponse;
import com.beautique.beautique.entity.Concern;
import com.beautique.beautique.entity.Ingredient;
import com.beautique.beautique.entity.ProductConcern;
import com.beautique.beautique.entity.ProductIngredient;
import com.beautique.beautique.enums.Category;
import com.beautique.beautique.enums.Source;
import com.beautique.beautique.entity.Product;
import com.beautique.beautique.repository.ConcernRepository;
import com.beautique.beautique.repository.IngredientRepository;
import com.beautique.beautique.repository.ProductConcernRepository;
import com.beautique.beautique.repository.ProductIngredientRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.beautique.beautique.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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

        List<Product> products = apiResponse.getProducts().stream()
                .map(this::mapToProduct)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
//        productRepository.saveAll(products);

        SephoraProductDetailsResponse productDetails;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ClassPathResource resource = new ClassPathResource("mocks/SephoraProductDetailsApiResponse.json");
            productDetails = objectMapper.readValue(resource.getInputStream(), SephoraProductDetailsResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Fetch all existing ingredients and concerns
        List<Ingredient> allIngredients = ingredientRepository.findAll();
        List<Concern> allConcerns = concernRepository.findAllByCategory(String.valueOf(Category.SKINCARE));

        Map<String, Ingredient> ingredientMap = allIngredients.stream()
                .collect(Collectors.toMap(i -> i.getName().toLowerCase(), i -> i, (i1, i2) -> i1));
        Map<String, Concern> concernMap = allConcerns.stream()
                .collect(Collectors.toMap(c -> c.getConcernName().toLowerCase(), c -> c, (c1, c2) -> c1));

        List<ProductIngredient> allProductIngredients = Collections.synchronizedList(new ArrayList<>());
        List<ProductConcern> allProductConcerns = Collections.synchronizedList(new ArrayList<>());

        // Process each product
        products.parallelStream().forEach(product -> {
//            SephoraProductDetailsResponse productDetails = fetchProductDetailsResponse(product.getProductId(), product.getSkuId());
//            if (productDetails == null) return;

            List<String> productIngredients = parseIngredients(productDetails.getCurrentSku().getIngredientDesc());
            List<String> productConcerns = parseSkincareConcerns(productDetails.getProductDetails().getShortDescription());

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

        // Batch insert product ingredients and concerns
        productIngredientRepository.saveAll(allProductIngredients);
        productConcernRepository.saveAll(allProductConcerns);
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

