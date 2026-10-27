package com.evershine.EvershineServer.securityConf.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
}
