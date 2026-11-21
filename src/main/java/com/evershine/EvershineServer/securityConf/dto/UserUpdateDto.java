package com.evershine.EvershineServer.securityConf.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class UserUpdateDto {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Address should be under 500 characters")
    private String address;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^(?:\\+?[1-9]\\d{7,14}|[6-9]\\d{9}|0[6-9]\\d{9})$",
            message = "Enter a valid phone number"
    )
    private String phoneNum;
}
