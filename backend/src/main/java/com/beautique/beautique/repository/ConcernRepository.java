package com.beautique.beautique.repository;

import com.beautique.beautique.entity.Concern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConcernRepository extends JpaRepository<Concern, Integer> {
    List<Concern> findAllByConcernNameInAndCategoryEquals(List<String> names, String category);

    List<Concern> findAllByCategory(String category);

}
