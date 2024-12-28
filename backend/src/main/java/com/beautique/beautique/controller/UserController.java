package com.beautique.beautique.controller;

import com.beautique.beautique.dto.UserRegistrationRequest;
import com.beautique.beautique.dto.UserProfileResponse;
import com.beautique.beautique.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // User Registration
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        userService.registerUser(userRegistrationRequest);
        return ResponseEntity.ok("User registered successfully.");
    }

    // User Login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.loginUser(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    // Get User Profile
    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long userId) {
        UserProfileResponse userProfile = userService.getUserProfile(userId);
        return ResponseEntity.ok(userProfile);
    }

    // Update User Profile
    @PutMapping("/{userId}/profile")
    public ResponseEntity<String> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody UserProfileResponse userProfileResponse) {
        userService.updateUserProfile(userId, userProfileResponse);
        return ResponseEntity.ok("User profile updated successfully.");
    }
}
