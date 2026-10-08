package com.evershine.EvershineServer.productApi.repository;

import com.evershine.EvershineServer.productApi.dto.ActiveFilterSummaryDTO;
import com.evershine.EvershineServer.productApi.entity.SubGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface SubGradeRepository extends JpaRepository<SubGrade, UUID> {
    boolean existsByName(String name);
    Optional<SubGrade> findByName(String name);
    List<SubGrade> findByActiveTrue();
    List<SubGrade> findByActiveFalse();

    @Query("""
    SELECT new com.evershine.EvershineServer.productApi.dto.ActiveFilterSummaryDTO(
        sg.id, sg.name, sg.active, COUNT(p.id)
    )
    FROM SubGrade sg
    LEFT JOIN Product p ON p.subGrade.id = sg.id
    GROUP BY sg.id, sg.name
""")
    List<ActiveFilterSummaryDTO> getSubGradeSummaries();
}
