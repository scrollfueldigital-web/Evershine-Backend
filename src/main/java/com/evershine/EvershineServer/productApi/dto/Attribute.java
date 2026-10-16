package com.evershine.EvershineServer.productApi.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter @Getter @RequiredArgsConstructor
public class Attribute {
    private String label;
    private String unit;
    private List<String> values;
    private Boolean customInput;
    private String min;
    private String max;
}
