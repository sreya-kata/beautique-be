package com.beautique.beautique.service;

import com.beautique.beautique.dto.auth.LoginRequest;
import com.beautique.beautique.dto.auth.LoginResponse;
import com.beautique.beautique.dto.auth.RegistrationRequest;
import com.beautique.beautique.entity.User;
import com.beautique.beautique.repository.UserRepository;
import com.beautique.beautique.util.JwtTokenUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthService(UserRepository userRepository, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public Integer register(RegistrationRequest registrationRequest) {
        // Hash the password
        String hashedPassword = passwordEncoder.encode(registrationRequest.getPassword());

        // Create a User entity and save to the database
        User user = new User();
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(hashedPassword); // Store the hashed password
        user.setName(registrationRequest.getName());
        userRepository.save(user);
        return user.getUserId();
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findUserByEmail(loginRequest.getEmail());
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtTokenUtil.generateToken(user);
        return new LoginResponse(token, user.getEmail(), user.getUserId());
    }

}

