package com.beautique.beautique.service;

import com.beautique.beautique.dto.UserLoginRequest;
import com.beautique.beautique.dto.UserLoginResponse;
import com.beautique.beautique.dto.UserRegistrationRequest;
import com.beautique.beautique.entity.User;
import com.beautique.beautique.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public void registerUser(UserRegistrationRequest userRegistrationRequest) {
        // Hash the password
        String hashedPassword = passwordEncoder.encode(userRegistrationRequest.getPassword());

        // Create a User entity and save to the database
        User user = new User();
        user.setEmail(userRegistrationRequest.getEmail());
        user.setPassword(hashedPassword); // Store the hashed password
        user.setName(userRegistrationRequest.getName());
        userRepository.save(user);
    }

    public UserLoginResponse loginUser(UserLoginRequest loginRequest) {
        // Find the user by email
        User user = userRepository.findUserByEmail(loginRequest.getEmail());
        if (user == null) throw new RuntimeException("Invalid email or password"));

        // Compare the provided password with the stored hashed password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate token or proceed with login
        String token = jwtTokenUtil.generateToken(user);
        return new UserLoginResponse(token, user.getEmail());
    }

}

