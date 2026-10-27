package com.evershine.EvershineServer.securityConf.service;

import com.evershine.EvershineServer.projectExceptionHandler.EmailException;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final Resend resend;

    @Value("${resend.from.email}")
    private String fromEmail;

    public void sendPasswordResetEmail(String recipientEmail, String resetLink) {
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <h2>Password Reset Request</h2>
                <p>We received a request to reset your account password.</p>
                <p>Click the button below to set a new password. This link is valid for 15 minutes:</p>
                <p>
                    <a href="%s" style="background-color: #007bff; color: white; padding: 10px 18px; text-decoration: none; border-radius: 4px; display: inline-block;">
                        Reset Password
                    </a>
                </p>
                <p>If you did not request this, you can safely ignore this email.</p>
            </div>
            """.formatted(resetLink);

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(fromEmail)
                .to(recipientEmail)
                .subject("Reset Your Password")
                .html(htmlContent)
                .build();

        try {
            CreateEmailResponse response = resend.emails().send(params);
            } catch (ResendException e) {
            // may throw custom exception
             throw new EmailException("Could not send password reset email. Please try again later.");
        }
    }
}