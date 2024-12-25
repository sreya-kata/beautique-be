package com.beautique.beautique.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class SephoraApiService {

    private final RestTemplate restTemplate;

    public SephoraApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<SephoraProductListResponse> fetchProductList(String category, Integer currentPage) {
        String listUrl = "https://sephora.p.rapidapi.com/us/products/v2/list?pageSize=60&currentPage=" + currentPage + "&categoryId=" + category;
        ResponseEntity<SephoraProductListResponse[]> response =
                restTemplate.getForEntity(listUrl, SephoraProductListResponse[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return Arrays.asList(response.getBody());
        } else {
            throw new RuntimeException("Failed to fetch product list from Sephora");
        }
    }

    public SephoraProductDetailsResponse fetchProductDetails(String productId, String skuId) {
        String productUrl = "https://api.sephora.com/v1/products";
        ResponseEntity<SephoraProductDetailsResponse> response =
                restTemplate.getForEntity(productUrl, SephoraProductDetailsResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to fetch product details from Sephora: " + productUrl);
        }
    }
}

