package com.evershine.EvershineServer.productApi.repository;

import com.evershine.EvershineServer.productApi.dto.FilterSummaryDTO;
import com.evershine.EvershineServer.productApi.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryTypeRepository extends JpaRepository<CategoryType, UUID> {
    boolean existsByName(String name);
    boolean existsBySlug(String name);
    List<CategoryType> findByCategory_Id(UUID categoryId);

    @Query("""
    SELECT new com.evershine.EvershineServer.productApi.dto.FilterSummaryDTO(
        c.id, c.name, COUNT(p.id)
    )
    FROM CategoryType c
    LEFT JOIN Product p ON p.categoryType.id = c.id
    GROUP BY c.id, c.name
""")
    List<FilterSummaryDTO> getCategoryTypeSummaries();
}
