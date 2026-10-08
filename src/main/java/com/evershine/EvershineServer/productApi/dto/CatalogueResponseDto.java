package com.evershine.EvershineServer.productApi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter @Getter @AllArgsConstructor
public class CatalogueResponseDto {
    private UUID id;
    private String name;
    private String slug;
}
