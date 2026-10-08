package com.evershine.EvershineServer.productApi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class CategoryTypeDto {
    private UUID id;
    private String name;
    private String slug;
    private List<CategoryVariantDto> variants;
}
