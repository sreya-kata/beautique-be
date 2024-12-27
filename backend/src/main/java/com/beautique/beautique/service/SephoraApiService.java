package com.beautique.beautique.service;

import com.beautique.beautique.dto.SephoraApiResponse;
import com.beautique.beautique.dto.SephoraProductDetailsResponse;
import com.beautique.beautique.dto.SephoraProductListResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class SephoraApiService {

    private final RestTemplate restTemplate;
    private final String rapidApiKey;
    private final String rapidApiHost;

    public SephoraApiService(RestTemplate restTemplate,
                             @Value("${rapidapi.key}") String rapidApiKey,
                             @Value("${rapidapi.host}") String rapidApiHost) {
        this.restTemplate = restTemplate;
        this.rapidApiKey = rapidApiKey;
        this.rapidApiHost = rapidApiHost;
    }

    public List<SephoraProductListResponse> fetchProductList(String category, Integer currentPage) {
        String listUrl = "https://sephora.p.rapidapi.com/us/products/v2/list?pageSize=60&currentPage=" + currentPage + "&categoryId=" + category;
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-host", rapidApiHost);
        headers.set("x-rapidapi-key", rapidApiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<SephoraApiResponse> response =
                restTemplate.exchange(listUrl, HttpMethod.GET, entity, SephoraApiResponse.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().getProducts();
        } else {
            throw new RuntimeException("Failed to fetch product list from Sephora");
        }
    }

    public SephoraProductDetailsResponse fetchProductDetails(String productId, String skuId) {
        String productUrl = "https://sephora.p.rapidapi.com/us/products/v2/detail?productId=" + productId + "&preferedSku=" + skuId;
        ResponseEntity<SephoraProductDetailsResponse> response =
                restTemplate.getForEntity(productUrl, SephoraProductDetailsResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to fetch product details from Sephora: " + productUrl);
        }
    }
}

