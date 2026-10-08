package com.evershine.EvershineServer.productApi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class FilterOptionsDto {
    private UUID categoryId;
    private UUID categoryTypeId;
    private UUID categoryVariantId;
    private UUID brandId;
    private UUID subBrandId;
    private UUID gradeId;
    private UUID subGradeId;
}
