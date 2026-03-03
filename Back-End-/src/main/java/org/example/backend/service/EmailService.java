package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    public void sendVaccinationAlert(String ownerEmail, String ownerName, String dogName, String vaccineName, String expiryDate) {
        String subject = "⚠️ Vaccination Expiry Alert - " + dogName;
        String body = String.format("""
                Dear %s,

                This is a reminder that your dog %s's vaccination (%s) is expiring on %s.

                Please schedule a vaccination appointment as soon as possible to keep your pet healthy.

                Best regards,
                Dog Daycare Team
                """, ownerName, dogName, vaccineName, expiryDate);
        sendEmail(ownerEmail, subject, body);
    }

    public void sendHealthAlert(String ownerEmail, String ownerName, String dogName, String healthStatus, String behaviour) {
        String subject = "🏥 Health Alert - " + dogName;
        String body = String.format("""
                Dear %s,

                We want to inform you that your dog %s has been assessed with the following status:
                
                Health Status: %s
                Behaviour: %s

                We recommend scheduling a veterinary consultation immediately.
                You can request a doctor appointment through our app.

                Best regards,
                Dog Daycare Team
                """, ownerName, dogName, healthStatus, behaviour);
        sendEmail(ownerEmail, subject, body);
    }

    public void sendAppointmentConfirmation(String to, String ownerName, String dogName, String date, String status) {
        String subject = "📅 Appointment " + status + " - " + dogName;
        String body = String.format("""
                Dear %s,

                Your daycare appointment for %s on %s has been %s.

                Thank you for choosing our services!

                Best regards,
                Dog Daycare Team
                """, ownerName, dogName, date, status.toLowerCase());
        sendEmail(to, subject, body);
    }
}