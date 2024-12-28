package com.beautique.beautique.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDetailsResponse {
    private String name;
    private String nickname;
    private String pronouns;
    private String gender;
    private Integer age;
}