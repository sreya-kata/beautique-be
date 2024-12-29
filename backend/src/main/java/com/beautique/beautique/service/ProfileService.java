package com.beautique.beautique.service;

import com.beautique.beautique.dto.SkincareProfileResponse;
import com.beautique.beautique.dto.UserDetailsResponse;
import com.beautique.beautique.entity.SkincareProfile;
import com.beautique.beautique.entity.User;
import com.beautique.beautique.enums.Category;
import com.beautique.beautique.repository.ConcernRepository;
import com.beautique.beautique.repository.SkincareProfileRepository;
import com.beautique.beautique.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import java.beans.FeatureDescriptor;
import java.util.Arrays;
import java.util.List;

@Service
public class ProfileService {
    private final UserRepository userRepository;
    private final SkincareProfileRepository skincareProfileRepository;
    private final ConcernRepository concernRepository;
    private final UserConcernService userConcernService;

    public ProfileService(UserRepository userRepository, SkincareProfileRepository skincareProfileRepository,
                          ConcernRepository concernRepository, UserConcernService userConcernService) {
        this.userRepository = userRepository;
        this.skincareProfileRepository = skincareProfileRepository;
        this.concernRepository = concernRepository;
        this.userConcernService = userConcernService;
    }

    public UserDetailsResponse getUserDetails(Integer userId) {
        User user = userRepository.findUserByUserId(userId);
        return new UserDetailsResponse(user.getName(), user.getNickname(), user.getPronouns(),
                user.getPronouns(), user.getAge());
    }

    public void updateUserDetails(Integer userId, UserDetailsResponse userDetailsResponse) {
        User user = userRepository.findUserByUserId(userId);
        copyNonNullProperties(userDetailsResponse, user);
        userRepository.save(user);
    }

    public SkincareProfileResponse getSkincareProfile(Integer userId) {
        SkincareProfile skincareProfile = skincareProfileRepository.findSkincareProfileByUserId(userId);
        List<String> concerns = concernRepository.findConcernNamesByUserId(userId, String.valueOf(Category.SKINCARE)); // Fetch concern names for the user
        return new SkincareProfileResponse(skincareProfile.getSkinType(), skincareProfile.getSkinTone(), skincareProfile.getBudget(),
                skincareProfile.getAllergiesSensitivities(), skincareProfile.getPrefersVegan(), skincareProfile.getPrefersCrueltyFree(),
                skincareProfile.getPrefersClean(), concerns);
    }

    public void createOrUpdateSkincareProfile(Integer userId, SkincareProfileResponse skincareProfileResponse) {
        // Create or update skincare profile
        saveOrUpdateSkincareProfile(userId, skincareProfileResponse);

        // Process user concerns
        userConcernService.processUserConcerns(userId, skincareProfileResponse.getConcerns());
    }

    private void saveOrUpdateSkincareProfile(Integer userId, SkincareProfileResponse skincareProfileResponse) {
        SkincareProfile skincareProfile = skincareProfileRepository.findSkincareProfileByUserId(userId);

        if (skincareProfile == null) {
            SkincareProfile newProfile = new SkincareProfile();
            populateSkincareProfile(newProfile, userId, skincareProfileResponse);
            skincareProfileRepository.save(newProfile);
        } else {
            copyNonNullProperties(skincareProfileResponse, skincareProfile);
            skincareProfileRepository.save(skincareProfile);
        }
    }

    private void populateSkincareProfile(SkincareProfile profile, Integer userId, SkincareProfileResponse response) {
        profile.setUserId(userId);
        profile.setSkinType(response.getSkinType());
        profile.setSkinTone(response.getSkinTone());
        profile.setBudget(response.getBudget());
        profile.setAllergiesSensitivities(response.getAllergiesSensitivities());
        profile.setPrefersVegan(response.getPrefersVegan());
        profile.setPrefersCrueltyFree(response.getPrefersCrueltyFree());
        profile.setPrefersClean(response.getPrefersClean());
    }

    private void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        return Arrays.stream(src.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> src.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

}
