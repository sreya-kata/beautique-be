package com.beautique.beautique.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SephoraProductListResponse {
    private String brandName;
    private String displayName;
    private String heroImage;
    private String productId;
    private String rating;
    private String targetUrl;
    private CurrentSku currentSku;

    @Data
    public static class CurrentSku {
        private String listPrice;
        private String imageAltText;
    }
}