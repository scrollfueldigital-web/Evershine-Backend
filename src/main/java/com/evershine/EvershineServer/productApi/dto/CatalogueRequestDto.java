package com.evershine.EvershineServer.productApi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter @AllArgsConstructor
public class CatalogueRequestDto {

    @NotEmpty(message = "Name should not be empty!")
    @Size(min = 2, max = 100,message = "Name should be between 2 to 100 characters")
    private String name;

    @NotEmpty(message = "Slug required!")
    @Size(min = 2,max = 100, message = "slug should be between 2 to 100 characters")
    private String slug;
}