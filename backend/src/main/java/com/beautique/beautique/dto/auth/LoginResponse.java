package com.beautique.beautique.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token; // JWT or another type of token
    private String email;
    private Integer userId;
}

