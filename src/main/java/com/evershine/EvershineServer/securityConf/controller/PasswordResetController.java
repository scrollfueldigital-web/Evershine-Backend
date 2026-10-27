package com.evershine.EvershineServer.securityConf.controller;

import com.evershine.EvershineServer.securityConf.dto.ForgotPasswordRequest;
import com.evershine.EvershineServer.securityConf.dto.ResetPasswordRequest;
import com.evershine.EvershineServer.securityConf.service.ForgotPasswordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PasswordResetController {

    private final ForgotPasswordService forgotPasswordService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest) {
        
            String ip = httpRequest.getRemoteAddr();

            forgotPasswordService.forgotPassword(
                    request.email(), ip);
            
        return ResponseEntity.ok(Map.of(
                "message",
                "If an account exists, reset instructions will be sent."
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        try {
            forgotPasswordService.resetPassword(
                    request.token(), request.newPassword());

            return ResponseEntity.ok(Map.of(
                    "message", "Password reset successfully."
            ));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Invalid or expired reset link."
            ));
        }
    }
}