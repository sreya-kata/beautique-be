package com.beautique.beautique.dto;

import lombok.Data;

import java.util.List;

@Data
public class SephoraApiResponse {
    private List<SephoraProductListResponse> products;
}
