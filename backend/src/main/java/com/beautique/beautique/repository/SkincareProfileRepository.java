package com.beautique.beautique.repository;

import com.beautique.beautique.entity.SkincareProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkincareProfileRepository extends JpaRepository<SkincareProfile, Integer> {
    SkincareProfile findSkincareProfileByUserId(Integer userId);
}
