package com.evershine.EvershineServer.securityConf.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class AppUserResponseDto {
    private String name;
    private String username;
    private String phoneNo;
    private String address;
}
