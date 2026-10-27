package com.evershine.EvershineServer.securityConf.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class RegisterResponseDto {
    private String username;
    private String phoneNum;
    private UUID id;
}
