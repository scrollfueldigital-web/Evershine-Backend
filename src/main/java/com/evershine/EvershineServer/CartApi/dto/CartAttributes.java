package com.evershine.EvershineServer.CartApi.dto;

import lombok.*;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString
public class CartAttributes {
    private String label;
    private String unit;
    private String value;
}
