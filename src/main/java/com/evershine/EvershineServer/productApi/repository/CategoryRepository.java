package com.evershine.EvershineServer.productApi.repository;

import com.evershine.EvershineServer.productApi.dto.FilterSummaryDTO;
import com.evershine.EvershineServer.productApi.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsBySlug(String slug);
    boolean existsByName(String name);


    @Query("""
    SELECT new com.evershine.EvershineServer.productApi.dto.FilterSummaryDTO(
        c.id, c.name, COUNT(p.id)
    )
    FROM Category c
    LEFT JOIN Product p ON p.category.id = c.id
    GROUP BY c.id, c.name
""")
    List<FilterSummaryDTO> getCategorySummaries();
}