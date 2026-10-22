package com.evershine.EvershineServer.CartApi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Setter @Getter
public class CartRequestDto{
    @NotNull(message = "user id is needed!")
    private UUID appUserId;

    @NotEmpty(message = "product slug is required!")
    private String productSlug;

    private List<CartAttributes> selectedCartAttributes;

    @NotNull(message = "Quantity is required!")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Long quantity;

}
