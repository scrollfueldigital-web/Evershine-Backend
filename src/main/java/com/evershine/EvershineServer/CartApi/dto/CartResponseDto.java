package com.evershine.EvershineServer.CartApi.dto;

import com.evershine.EvershineServer.productApi.dto.Attribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class CartResponseDto {
    private UUID id;
    private String productImageUrl;
    private String productSlug;
    private String productName;
    private UUID productBrandId;
    private List<CartAttributes> selectedAttributes;
    private List<Attribute> existingAttributes;
    private Long quantity;
}
