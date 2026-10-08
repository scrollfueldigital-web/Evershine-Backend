package com.evershine.EvershineServer.productApi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;
@Setter
@Getter
@RequiredArgsConstructor
public class PropertyRequestDto {
    private String heading;
    private Map<String,String> tableOfContent;
}
