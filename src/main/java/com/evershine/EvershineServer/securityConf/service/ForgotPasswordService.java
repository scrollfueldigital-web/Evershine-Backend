package com.evershine.EvershineServer.securityConf.service;

import com.evershine.EvershineServer.securityConf.entity.AppUser;
import com.evershine.EvershineServer.securityConf.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final AppUserRepository userRepository;
    private final RateLimitService rateLimitService;
    private final ResetPasswordTokenService tokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void forgotPassword(String email, String ip) {
        String username = email.trim().toLowerCase(Locale.ROOT);

        // Count IP and email requests.
        if (!rateLimitService.allowForgotRequest(ip, username)) {
            return;
        }

        // Your AppUser.username field stores the email.
        AppUser user = userRepository.findByUsername(username)
                .orElse(null);

        // Same API response whether the account exists or not.
        if (user == null) {
            return;
        }

        if (tokenService.hasValidToken(username)) {
            return;
        }

        if (!rateLimitService.allowEmailSend(username)) {
            return;
        }

        String token = tokenService.createToken(username);

        String baseUrl = frontendUrl.replaceAll("/+$", "");
        String resetLink =
                baseUrl + "/reset-password?token=" + token;

        try {
            // Change this call to match your existing EmailService.
            emailService.sendPasswordResetEmail(username, resetLink);
        } catch (Exception ex) {
            // Invalidate the token if email delivery fails.
            tokenService.consumeToken(token);
            throw new IllegalStateException(
                    "Could not send password reset email", ex);
        }
    }

    public void resetPassword(
            String token, String newPassword) {

        String username = tokenService.consumeToken(token)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid or expired reset link"));

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid or expired reset link"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
