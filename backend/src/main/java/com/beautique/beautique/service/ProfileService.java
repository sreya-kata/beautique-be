package com.beautique.beautique.service;

import com.beautique.beautique.dto.UserDetailsResponse;
import com.beautique.beautique.entity.User;
import com.beautique.beautique.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import java.beans.FeatureDescriptor;
import java.util.Arrays;

@Service
public class ProfileService {
    private final UserRepository userRepository;

    public ProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
