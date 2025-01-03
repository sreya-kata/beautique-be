package com.beautique.beautique.repository;

import com.beautique.beautique.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByEmail(String email);

    User findUserByUserId(Integer userId);

    @Modifying
    @Query("DELETE FROM UserConcern uc WHERE uc.userId = :userId")
    void deleteByUserId(@Param("userId") Integer userId);
}

