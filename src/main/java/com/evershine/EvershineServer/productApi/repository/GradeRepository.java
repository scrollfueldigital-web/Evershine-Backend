package com.evershine.EvershineServer.productApi.repository;

import com.evershine.EvershineServer.productApi.dto.ActiveFilterSummaryDTO;
import com.evershine.EvershineServer.productApi.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface GradeRepository extends JpaRepository<Grade, UUID> {
    boolean existsByName(String name);
    Optional<Grade> findByName(String name);
    List<Grade> findByActiveTrue();
    List<Grade> findByActiveFalse();

    @Query("""
    SELECT new com.evershine.EvershineServer.productApi.dto.ActiveFilterSummaryDTO(
        g.id, g.name, g.active, COUNT(p.id)
    )
    FROM Grade g
    LEFT JOIN Product p ON p.grade.id = g.id
    GROUP BY g.id, g.name
""")
    List<ActiveFilterSummaryDTO> getGradeSummaries();
}