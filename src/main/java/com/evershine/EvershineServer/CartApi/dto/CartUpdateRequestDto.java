package com.evershine.EvershineServer.CartApi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class CartUpdateRequestDto {
    @Min(value = 1, message = "quantity should be more than zero")
    private Long quantity;

    @NotNull(message = "User id is required!")
    private UUID userId;

    @NotNull(message = "product slug is required!")
    private String productSlug;

    private List<CartAttributes> cartAttributes;
}
