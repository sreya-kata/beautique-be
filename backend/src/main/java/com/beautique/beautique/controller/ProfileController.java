package com.beautique.beautique.controller;

import com.beautique.beautique.dto.UserDetailsResponse;
import com.beautique.beautique.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // Get User Profile
    @GetMapping("/{userId}/details")
    public ResponseEntity<UserDetailsResponse> getUserProfile(@PathVariable Integer userId) {
        UserDetailsResponse userDetails = profileService.getUserDetails(userId);
        return ResponseEntity.ok(userDetails);
    }

    // Update User Profile
    @PutMapping("/{userId}/update")
    public ResponseEntity<String> updateUser(
            @PathVariable Integer userId,
            @RequestBody UserDetailsResponse userDetailsResponse) {
        profileService.updateUserDetails(userId, userDetailsResponse);
        return ResponseEntity.ok("User profile updated successfully.");
    }

//    // Get Skincare Profile
//    @GetMapping("/{userId}/skincare-profile")
//    public ResponseEntity<String> updateUserProfile(
//            @PathVariable Integer userId,
//            @RequestBody SkincareProfileResponse userProfileResponse) {
//        userService.updateUserProfile(userId, userProfileResponse);
//        return ResponseEntity.ok("User profile updated successfully.");
//    }
//
//    // Update or Create Skincare Profile
//    @PutMapping("/{userId}/skincare-profile")
//    public ResponseEntity<String> updateUserProfile(
//            @PathVariable Integer userId,
//            @RequestBody SkincareProfileResponse userProfileResponse) {
//        userService.updateUserProfile(userId, userProfileResponse);
//        return ResponseEntity.ok("User profile updated successfully.");
//    }
}
