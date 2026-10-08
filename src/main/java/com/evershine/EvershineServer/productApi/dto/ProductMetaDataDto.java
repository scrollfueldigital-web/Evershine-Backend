package com.evershine.EvershineServer.productApi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductMetaDataDto {
    private UUID id;
    private String imageUrl;
    private String title;

    private List<AttributeDto> attributes;

    private UUID categoryId;
    private UUID categoryTypeId;
    private UUID categoryVariantId;
    private UUID brandId;
    private UUID subBrandId;
    private UUID gradeId;
    private UUID subGradeId;
}