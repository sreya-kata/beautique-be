package com.beautique.beautique.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SephoraProductDetailsResponse {
    private CurrentSku currentSku;
    private ProductDetails productDetails;

    @Data
    public static class CurrentSku {
        private List<Highlight> highlights;
        private String ingredientDesc;

        @Data
        public static class Highlight {
            private String id;
            private String altText;
            private String name;
            private String imageUrl;
        }
    }

    @Data
    public static class ProductDetails {
        private String shortDescription;

    }
}
