package com.evershine.EvershineServer.CartApi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Setter @Getter
public class CartRequestDto{
    private UUID appUserId;
    private String productSlug;
    private List<CartAttributes> selectedCartAttributes;
    private Long quantity;
}
