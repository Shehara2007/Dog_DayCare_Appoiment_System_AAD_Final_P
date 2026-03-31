package org.example.backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupTestMailRunner implements ApplicationRunner {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.mail.startup-test.enabled:true}")
    private boolean enabled;

    @Value("${app.mail.startup-test.to:rayff60@gmail.com}")
    private String toEmail;

    @Value("${app.mail.startup-test.subject:PawCare Startup Test Email}")
    private String subject;

    @Value("${app.mail.from:${spring.mail.username:}}")
    private String fromEmail;

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            log.info("Startup test mail is disabled.");
            return;
        }

        if (toEmail == null || toEmail.trim().isEmpty()) {
            log.warn("Startup test mail receiver is empty. Skipping test email.");
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("JavaMailSender bean is not available. Startup test email skipped.");
            return;
        }

        try {
            String now = LocalDateTime.now().toString();
            String html = "<!doctype html><html><body style='margin:0;padding:0;background:#f3f4f6;font-family:Arial,sans-serif;color:#1f2937;'>"
                    + "<div style='max-width:640px;margin:24px auto;background:#ffffff;border:1px solid #e5e7eb;border-radius:12px;overflow:hidden;'>"
                    + "<div style='background:#2563eb;color:#ffffff;padding:14px 20px;font-size:20px;font-weight:700;'>PawCare</div>"
                    + "<div style='padding:20px;'>"
                    + "<h2 style='margin:0 0 12px 0;font-size:22px;'>" + escape(subject) + "</h2>"
                    + "<p style='margin:0 0 10px 0;'>Hello,</p>"
                    + "<p style='margin:0 0 12px 0;color:#4b5563;'>Application startup mail test is successful.</p>"
                    + "<p style='margin:0 0 8px 0;'><strong>Time:</strong> " + escape(now) + "</p>"
                    + "</div></div></body></html>";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail.trim());
            if (StringUtils.hasText(fromEmail)) {
                helper.setFrom(fromEmail.trim());
            }
            helper.setSubject(subject);
            helper.setText("This is a startup test email from PawCare.\n\nTime: " + now, html);

            mailSender.send(message);
            log.info("Startup test email sent to {}", toEmail);
        } catch (Exception ex) {
            log.error("Failed to send startup test email to {}", toEmail, ex);
        }
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

