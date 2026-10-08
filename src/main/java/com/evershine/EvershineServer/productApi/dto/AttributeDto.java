package com.evershine.EvershineServer.productApi.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AttributeDto {
    private String label;
    private String unit;
    private String min;
    private String max;
}
