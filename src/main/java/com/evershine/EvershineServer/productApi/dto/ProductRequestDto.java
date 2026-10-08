package com.evershine.EvershineServer.productApi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class ProductRequestDto {
    @NotEmpty(message = "Product title is required!")
    @Size(max = 150, min=2,message = "Title should be between 150 to 2 characters")
    private String title;

    @NotEmpty(message = "Slug title is required!")
    @Size(max = 150, min=4,message = "Slug should be between 4 to 150 characters")
    private String slug;

    @NotEmpty(message = "Please add image url!")
    private String imageUrl;
    private ProductStatus productStatus;

    @NotEmpty(message = "Attributes of products are required!")
    @Valid
    private List<AttributeRequestDto> attributeRequestDtos;

    @NotEmpty(message = "Overview of product is required!")
    private String overviewHtml;

    private List<PropertyTable> propertyTables;

    private UUID categoryId;
    private UUID categoryTypeId;
    private UUID categoryVariantId;
    private UUID brandId;
    private UUID subBrandId;
    private UUID gradeId;
    private UUID subGradeId;
}
