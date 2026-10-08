package com.evershine.EvershineServer.productApi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ProductLightResponseDto {
    private UUID id;
    private String title;
    private String imageUrl;
    private ProductStatus productStatus;

    private List<AttributeDto> attributeDtoList;
    private String overviewHtml;
    private List<PropertyRequestDto> propertyRequestDtoList;
    private UUID categoryId;
    private UUID categoryTypeId;
    private UUID categoryVariantId;

    private UUID brandId;
    private String brandName;
    private UUID subBrandId;
    private UUID gradeId;
    private UUID subGradeId;
}