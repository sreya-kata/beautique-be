package com.beautique.beautique.dto.sephora;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SephoraApiResponse {
    private List<SephoraProductListResponse> products;
}
