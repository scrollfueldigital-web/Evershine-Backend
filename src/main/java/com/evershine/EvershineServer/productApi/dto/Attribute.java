package com.evershine.EvershineServer.productApi.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter @Getter @RequiredArgsConstructor @ToString
public class Attribute {
    private String label;
    private String unit;
    private List<String> values;
    private Boolean customInput;
    private Double min;
    private Double max;
}
