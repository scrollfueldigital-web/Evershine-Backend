package com.evershine.EvershineServer.securityConf.controller;

import com.evershine.EvershineServer.securityConf.dto.AuthResponse;
import com.evershine.EvershineServer.securityConf.dto.LoginRequestDto;
import com.evershine.EvershineServer.securityConf.dto.RegisterRequestDto;
import com.evershine.EvershineServer.securityConf.dto.RegisterResponseDto;
import com.evershine.EvershineServer.securityConf.service.AuthService;
import com.evershine.EvershineServer.securityConf.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(
            @RequestBody @Valid RegisterRequestDto request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody @Valid LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
