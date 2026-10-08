package com.evershine.EvershineServer.productApi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class AttributeRequestDto {
    @NotEmpty(message = "Please add label!")
    @Size(min = 2, max = 20, message = "Label should be between 2 to 20 characters")
    private String label;

    @Size(max = 10, message = "Unit should be between 2 to 10 characters")
    private String unit;

    @NotEmpty(message = "Available all size are required!")
    private List<String> values;

    private Boolean customInput;

    private String min;

    private String max;
}
