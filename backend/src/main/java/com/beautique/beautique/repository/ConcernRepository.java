package com.beautique.beautique.repository;

import com.beautique.beautique.entity.Concern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConcernRepository extends JpaRepository<Concern, Integer> {
    List<Concern> findAllByConcernNameInAndCategoryEquals(List<String> names, String category);

    List<Concern> findAllByCategory(String category);

    @Query("SELECT c.concernName FROM Concern c INNER JOIN UserConcern uc ON c.concernId = uc.concernId WHERE uc.userId = :userId AND c.category = :category")
    List<String> findConcernNamesByUserId(@Param("userId") Integer userId, @Param("category") String category);

    @Query("SELECT c.concernId FROM Concern c WHERE c.concernName IN :names AND c.category = :category")
    List<Integer> findConcernIdsByNamesAndCategory(@Param("names") List<String> names, String category);
}
