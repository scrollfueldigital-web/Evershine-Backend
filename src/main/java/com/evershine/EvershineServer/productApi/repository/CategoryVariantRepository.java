package com.evershine.EvershineServer.productApi.repository;

import com.evershine.EvershineServer.productApi.dto.FilterSummaryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.evershine.EvershineServer.productApi.entity.CategoryVariant;

import java.util.List;
import java.util.UUID;
@Repository
public interface CategoryVariantRepository extends
        JpaRepository<CategoryVariant, UUID> {

    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    List<CategoryVariant> findByCategoryType_Id(UUID categoryTypeId);
    @Query("""
    SELECT new com.evershine.EvershineServer.productApi.dto.FilterSummaryDTO(
        c.id, c.name, COUNT(p.id)
    )
    FROM CategoryVariant c
    LEFT JOIN Product p ON p.categoryVariant.id = c.id
    GROUP BY c.id, c.name
""")
    List<FilterSummaryDTO> getCategoryVariantSummaries();
}
