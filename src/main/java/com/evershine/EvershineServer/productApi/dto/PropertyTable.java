package com.evershine.EvershineServer.productApi.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter @Getter @RequiredArgsConstructor
public class PropertyTable {
    private String heading;
    private Map<String,String> tableOfContent;
}
