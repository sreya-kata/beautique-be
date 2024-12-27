package com.beautique.beautique.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SephoraProductDetailsResponse {
    private CurrentSku currentSku;
    private ProductDetails productDetails;
    private String productId;

    @Data
    public static class CurrentSku {
        private String ingredientDesc;
    }

    @Data
    public static class ProductDetails {
        private String shortDescription;
        private String longDescription;
    }
}
