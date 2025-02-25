package com.beautique.beautique.service;

import com.beautique.beautique.model.OpenAIRequest;
import com.beautique.beautique.model.OpenAIResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenAIService {

    @Value("${openai.api-key}")
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public OpenAIResponse sendRequest(OpenAIRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<OpenAIRequest> requestEntity = new HttpEntity<>(request, headers);

        return restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, OpenAIResponse.class).getBody();
    }
}
