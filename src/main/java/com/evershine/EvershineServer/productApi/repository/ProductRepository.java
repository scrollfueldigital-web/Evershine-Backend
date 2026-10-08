package com.evershine.EvershineServer.productApi.repository;

import com.evershine.EvershineServer.productApi.dto.ProductStatus;
import com.evershine.EvershineServer.productApi.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    // Returns all products linked to this specific brand ID
    List<Product>findByBrandId(UUID id);
    List<Product>findBySubBrandId(UUID id);
    List<Product>findByGradeId(UUID id);
    List<Product>findBySubGradeId(UUID id);

    long countByProductStatus(ProductStatus status);

    // find full products
    @Query("""
    SELECT DISTINCT p
    FROM Product p
    LEFT JOIN FETCH p.category
    LEFT JOIN FETCH p.categoryType
    LEFT JOIN FETCH p.categoryVariant
    LEFT JOIN FETCH p.brand
    LEFT JOIN FETCH p.subBrand
    LEFT JOIN FETCH p.grade
    LEFT JOIN FETCH p.subGrade
""")
    List<Product> findAllFull();

    @EntityGraph(attributePaths = {
            "category",
            "categoryType",
            "categoryVariant",
            "brand",
            "subBrand",
            "grade",
            "subGrade"
    })
    List<Product> findAll();

    List<Product> findByProductStatus(ProductStatus status);

    boolean existsBySlug(String slug);
}
