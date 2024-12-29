package com.beautique.beautique.service;

import com.beautique.beautique.entity.UserConcern;
import com.beautique.beautique.enums.Category;
import com.beautique.beautique.repository.ConcernRepository;
import com.beautique.beautique.repository.UserConcernRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserConcernService {
    private final UserConcernRepository userConcernRepository;
    private final ConcernRepository concernRepository;

    public UserConcernService(UserConcernRepository userConcernRepository, ConcernRepository concernRepository) {
        this.userConcernRepository = userConcernRepository;
        this.concernRepository = concernRepository;
    }

    @Transactional
    public void processUserConcerns(Integer userId, List<String> concerns) {
        if (concerns == null || concerns.isEmpty()) {
            userConcernRepository.deleteByUserId(userId);
            return;
        }

        List<Integer> concernIds = concernRepository.findConcernIdsByNamesAndCategory(concerns, String.valueOf(Category.SKINCARE));
        userConcernRepository.deleteByUserId(userId);

        List<UserConcern> userConcerns = concernIds.stream()
                .map(concernId -> new UserConcern(userId, concernId))
                .toList();

        userConcernRepository.saveAll(userConcerns);
    }
}
