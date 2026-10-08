package com.evershine.EvershineServer.productApi.repository;

import com.evershine.EvershineServer.productApi.dto.ActiveFilterSummaryDTO;
import com.evershine.EvershineServer.productApi.entity.SubBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface SubBrandRepository extends JpaRepository<SubBrand, UUID> {
    boolean existsByName(String name);
    Optional<SubBrand> findByName(String name);
    List<SubBrand> findByActiveFalse();
    List<SubBrand> findByActiveTrue();

    @Query("""
    SELECT new com.evershine.EvershineServer.productApi.dto.ActiveFilterSummaryDTO(
        sb.id, sb.name, sb.active, COUNT(p.id)
    )
    FROM SubBrand sb
    LEFT JOIN Product p ON p.subBrand.id = sb.id
    GROUP BY sb.id, sb.name
""")
    List<ActiveFilterSummaryDTO> getSubBrandSummaries();
}
