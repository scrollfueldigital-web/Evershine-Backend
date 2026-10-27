package com.evershine.EvershineServer.securityConf.dto;


import jakarta.validation.constraints.*;
import lombok.*;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class RegisterRequestDto {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotEmpty(message = "Password required")
    @Size(max = 16,min = 8, message = "Password must be in between 16 to 8 characters")
    private String password;

    private String address;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 254, message = "Email cannot exceed 254 characters")
    private String username;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^(?:\\+?[1-9]\\d{7,14}|[6-9]\\d{9}|0[6-9]\\d{9})$",
            message = "Enter a valid phone number"
    )
    private String phoneNum;
}