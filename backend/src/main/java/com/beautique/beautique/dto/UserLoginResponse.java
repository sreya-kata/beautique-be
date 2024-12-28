package com.beautique.beautique.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginResponse {
    private String token; // JWT or another type of token
    private String email;
}

