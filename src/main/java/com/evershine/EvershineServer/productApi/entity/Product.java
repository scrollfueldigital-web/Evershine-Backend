package com.evershine.EvershineServer.productApi.entity;

import com.evershine.EvershineServer.productApi.dto.Attribute;
import com.evershine.EvershineServer.productApi.dto.ProductStatus;
import com.evershine.EvershineServer.productApi.dto.PropertyTable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter @RequiredArgsConstructor
@Table(
        indexes = {
                    @Index(columnList = "product_status"),
                    @Index(columnList = "brand_id"),
                    @Index(columnList = "sub_brand_id"),
                    @Index(columnList = "grade_id"),
                    @Index(columnList = "sub_grade_id"),
                    @Index(columnList = "category_type_id")
                // may need index over category
        })
@Data @ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    private CategoryType categoryType;

    @ManyToOne(fetch = FetchType.LAZY)
    private CategoryVariant categoryVariant;

    @ManyToOne(fetch = FetchType.LAZY)
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    private SubBrand subBrand;

    @ManyToOne(fetch = FetchType.LAZY)
    private Grade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    private SubGrade subGrade;

    private String imageUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<Attribute> attributes;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String overviewHtml;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<PropertyTable> propertyTables;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus = ProductStatus.ACTIVE;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
