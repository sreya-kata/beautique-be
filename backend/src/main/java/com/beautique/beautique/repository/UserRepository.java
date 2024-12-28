package com.beautique.beautique.repository;

import com.beautique.beautique.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findUserByEmail(String email);

    User findUserByUserId(Integer userId);
}

