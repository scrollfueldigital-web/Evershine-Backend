package com.evershine.EvershineServer.securityConf.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LoginRequestDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 254)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(max = 16, min = 8, message = "Password must be in between 8 to 16 characters")
    private String password;
}
