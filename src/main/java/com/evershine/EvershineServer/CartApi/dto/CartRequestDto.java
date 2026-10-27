package com.evershine.EvershineServer.CartApi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@AllArgsConstructor @NoArgsConstructor
@Setter @Getter
public class CartRequestDto {
    @NotBlank(message = "Product slug is required")
    private String productSlug;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Long quantity;

    private List<CartAttributes> selectedCartAttributes;
}
