package com.evershine.EvershineServer.productApi.repository;

import com.evershine.EvershineServer.productApi.dto.ActiveFilterSummaryDTO;
import com.evershine.EvershineServer.productApi.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {
    boolean existsByName(String name);
    Optional<Brand> findByName(String name);
    @Query("""
    SELECT new com.evershine.EvershineServer.productApi.dto.ActiveFilterSummaryDTO(
        b.id, b.name, b.active, COUNT(p.id)
    )
    FROM Brand b
    LEFT JOIN Product p ON p.brand.id = b.id
    GROUP BY b.id, b.name
""")
    List<ActiveFilterSummaryDTO> getBrandSummaries();

    List<Brand> findByActiveTrue();
    List<Brand> findByActiveFalse();
}
