package com.cliniccare.backend.auth;

import com.cliniccare.backend.user.AppUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "cliniccare.mail", name = "enabled", havingValue = "true")
public class EmailPasswordResetDelivery implements PasswordResetDelivery {
    private final JavaMailSender mailSender;
    private final String senderAddress;

    public EmailPasswordResetDelivery(
            JavaMailSender mailSender,
            @Value("${cliniccare.mail.from}") String senderAddress) {
        this.mailSender = mailSender;
        this.senderAddress = senderAddress;
    }

    @Override
    public void send(AppUser user, String resetUrl) {
        if (user.getEmail() == null || user.getEmail().isBlank()) return;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderAddress);
        message.setTo(user.getEmail());
        message.setSubject("ClinicCare password reset");
        message.setText("""
                A password reset was requested for your ClinicCare account.

                Use this one-time link within 30 minutes:
                %s

                If you did not request this, you can ignore this message.
                """.formatted(resetUrl));
        mailSender.send(message);
    }
}
