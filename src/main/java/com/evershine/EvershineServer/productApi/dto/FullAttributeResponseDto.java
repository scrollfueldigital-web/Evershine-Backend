package com.evershine.EvershineServer.productApi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class FullAttributeResponseDto {
    private String label;
    private String unit;
    private String min;
    private String max;
    private List<String> values;
}
