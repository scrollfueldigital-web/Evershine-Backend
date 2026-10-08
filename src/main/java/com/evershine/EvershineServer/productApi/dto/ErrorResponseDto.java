package com.evershine.EvershineServer.productApi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter @Getter @AllArgsConstructor
public class ErrorResponseDto {

    @Schema(description = "Time when the error happened", example = "2026-10-01T12:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP Status Code", example = "400")
    private int status;

    @Schema(description = "The explanation of what went wrong", example = "Brand name already exists")
    private String message;
}

