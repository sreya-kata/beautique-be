package com.beautique.beautique.dto.sephora;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SephoraProductDetailsResponse {
    private CurrentSku currentSku;
    private ParentCategory parentCategory;
    private ProductDetails productDetails;
    private String productId;

    @Data
    public static class CurrentSku {
        private String ingredientDesc;
    }

    @Data
    public static class ParentCategory {
        private String displayName;
    }

    @Data
    public static class ProductDetails {
        private String shortDescription;
        private String longDescription;
        private String suggestedUsage;
    }
}
