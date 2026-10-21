package com.evershine.EvershineServer.CartApi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class CartUpdateRequestDto {
    private Long quantity;
    private UUID userId;
    private String productSlug;
    private List<CartAttributes> cartAttributes;
}
