package com.beautique.beautique.dto.auth;

import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Data
public class RegistrationRequest {

    @NotNull(message = "Email is required.")
    @Email(message = "Please provide a valid email address.")
    private String email;

    @NotNull(message = "Password is required.")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters.")
    private String password;

    @NotNull(message = "Name is required.")
    @Size(max = 50, message = "Name cannot exceed 50 characters.")
    private String name;

    @Size(max = 50, message = "Nickname cannot exceed 50 characters.")
    private String nickname;
    private String pronouns;
    private Integer age;
    private String gender;
}