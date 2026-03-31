package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.Dog;
import org.example.backend.Entity.Notification;
import org.example.backend.Entity.User;
import org.example.backend.EnumPackage.NotificationType;
import org.example.backend.Repository.NotificationRepository;
import org.example.backend.exeception.BusinessException;
import org.example.backend.service.NotificationService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.List;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.mail.from:${spring.mail.username:}}")
    private String fromEmail;

    @Override
    public Notification create(User owner, Dog dog, NotificationType type, String message) {
        Notification notification = new Notification();
        notification.setOwner(owner);
        notification.setDog(dog);
        notification.setType(type);
        notification.setMessage(message);

        sendStyledAlertEmail(owner, dog, type, message);

        return notificationRepository.save(notification);
    }

    @Override
    public void sendDogRegistrationQr(User owner, Dog dog, String qrScanUrl) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null || owner == null || !StringUtils.hasText(owner.getEmail())) {
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(owner.getEmail());
            if (StringUtils.hasText(fromEmail)) {
                helper.setFrom(fromEmail.trim());
            }
            helper.setSubject("Dog Registration QR Code");
            helper.setText(buildQrEmailTextBody(owner, dog, qrScanUrl), buildQrEmailBody(owner, dog, qrScanUrl));

            if (StringUtils.hasText(dog.getQrCodeBase64())) {
                byte[] qrImage = Base64.getDecoder().decode(dog.getQrCodeBase64());
                helper.addInline("dogQrImage", new ByteArrayResource(qrImage), "image/png");
            }

            mailSender.send(mimeMessage);
        } catch (MessagingException | IllegalArgumentException ex) {
            throw new BusinessException("Failed to send dog QR email");
        }
    }

    @Override
    public List<Notification> getByOwner(Long ownerId) {
        return notificationRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId);
    }

    private void sendStyledAlertEmail(User owner, Dog dog, NotificationType type, String message) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null || owner == null || !StringUtils.hasText(owner.getEmail())) {
            return;
        }

        String subject = type == NotificationType.VACCINATION_ALERT ? "Vaccination Expiry Alert" : "Dog Health Alert";
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(owner.getEmail());
            if (StringUtils.hasText(fromEmail)) {
                helper.setFrom(fromEmail.trim());
            }
            helper.setSubject(subject);
            helper.setText(buildAlertEmailText(owner, dog, type, message), buildAlertEmailBody(owner, dog, type, message));
            mailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            throw new BusinessException("Failed to send notification email");
        }
    }

    private String buildAlertEmailBody(User owner, Dog dog, NotificationType type, String message) {
        StringBuilder body = new StringBuilder();
        body.append("<!doctype html><html><body style='margin:0;padding:0;background:#f3f4f6;font-family:Arial,sans-serif;color:#1f2937;'>")
                .append("<div style='max-width:640px;margin:24px auto;background:#ffffff;border:1px solid #e5e7eb;border-radius:12px;overflow:hidden;'>")
                .append("<div style='background:#2563eb;color:#ffffff;padding:14px 20px;font-size:20px;font-weight:700;'>PawCare</div>")
                .append("<div style='padding:20px;'>")
                .append("<h2 style='margin:0 0 12px 0;font-size:22px;'>")
                .append(type == NotificationType.VACCINATION_ALERT ? "Vaccination Expiry Alert" : "Dog Health Alert")
                .append("</h2>")
                .append("<p style='margin:0 0 12px 0;'>Hello ").append(escape(owner.getName())).append(",</p>")
                .append("<p style='margin:0 0 14px 0;color:#4b5563;'>")
                .append(type == NotificationType.VACCINATION_ALERT
                        ? "Your dog's vaccination is expiring soon."
                        : "There is an important health update for your dog.")
                .append("</p>");

        if (dog != null) {
            body.append(infoRow("Dog Name", dog.getName()));
            body.append(infoRow("Dog ID", String.valueOf(dog.getId())));
        }

        body.append(infoRow("Notification Type", String.valueOf(type)));
        body.append(infoRow("Details", message));
        body.append("<p style='margin:16px 0 0 0;color:#6b7280;font-size:13px;'>Please contact daycare support if you need assistance.</p>")
                .append("</div></div></body></html>");
        return body.toString();
    }

    private String buildAlertEmailText(User owner, Dog dog, NotificationType type, String message) {
        StringBuilder body = new StringBuilder();
        body.append("Hello ").append(owner.getName()).append(",\n\n");
        body.append(type == NotificationType.VACCINATION_ALERT
                ? "Your dog's vaccination is expiring soon.\n"
                : "There is an important health update for your dog.\n");
        if (dog != null) {
            body.append("Dog Name: ").append(dog.getName()).append("\n");
            body.append("Dog ID: ").append(dog.getId()).append("\n");
        }
        body.append("Notification Type: ").append(type).append("\n");
        body.append("Details: ").append(message).append("\n\n");
        body.append("- PawCare Team");
        return body.toString();
    }

    private String buildQrEmailBody(User owner, Dog dog, String qrScanUrl) {
        String dogName = dog != null && StringUtils.hasText(dog.getName()) ? dog.getName() : "Your dog";
        StringBuilder body = new StringBuilder();
        body.append("<!doctype html><html><body style='margin:0;padding:0;background:#f3f4f6;font-family:Arial,sans-serif;color:#1f2937;'>")
                .append("<div style='max-width:640px;margin:24px auto;background:#ffffff;border:1px solid #e5e7eb;border-radius:12px;overflow:hidden;'>")
                .append("<div style='background:#2563eb;color:#ffffff;padding:14px 20px;font-size:20px;font-weight:700;'>PawCare</div>")
                .append("<div style='padding:20px;'>")
                .append("<h2 style='margin:0 0 12px 0;font-size:22px;'>Dog Registration QR Code</h2>")
                .append("<p style='margin:0 0 12px 0;'>Hello ").append(escape(owner.getName())).append(",</p>")
                .append("<p style='margin:0 0 14px 0;color:#4b5563;'>")
                .append(escape(dogName)).append(" has been registered successfully.</p>")
                .append("<p style='margin:0 0 14px 0;color:#4b5563;'>Use the QR code below to quickly open health reports.</p>")
                .append("<p style='margin:0 0 14px 0;'><img src='cid:dogQrImage' alt='Dog QR Code' style='max-width:220px;border:1px solid #e5e7eb;border-radius:8px;padding:6px;background:#ffffff;'/></p>");

        if (StringUtils.hasText(qrScanUrl)) {
            body.append("<p style='margin:0 0 10px 0;'><strong>Direct link:</strong> <a href='")
                    .append(escape(qrScanUrl)).append("'>")
                    .append(escape(qrScanUrl)).append("</a></p>")
                    .append("<p style='margin:10px 0 0 0;'><a href='")
                    .append(escape(qrScanUrl)).append("' style='display:inline-block;background:#2563eb;color:#ffffff;text-decoration:none;padding:10px 14px;border-radius:8px;'>Open Dog Health Reports</a></p>");
        }
        body.append("<p style='margin:16px 0 0 0;color:#6b7280;font-size:13px;'>- PawCare Team</p>")
                .append("</div></div></body></html>");
        return body.toString();
    }

    private String buildQrEmailTextBody(User owner, Dog dog, String qrScanUrl) {
        String dogName = dog != null && StringUtils.hasText(dog.getName()) ? dog.getName() : "Your dog";
        StringBuilder text = new StringBuilder();
        text.append("Hello ").append(owner.getName()).append(",\n\n");
        text.append(dogName).append(" has been registered successfully.\n");
        text.append("Use the QR image in this email to open health reports.\n");
        if (StringUtils.hasText(qrScanUrl)) {
            text.append("Direct link: ").append(qrScanUrl).append("\n");
        }
        text.append("\n- PawCare Team");
        return text.toString();
    }

    private String infoRow(String label, String value) {
        return "<p style='margin:0 0 8px 0;line-height:1.5;'><strong>" + escape(label) + ":</strong> " + escape(value) + "</p>";
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}

