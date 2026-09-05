package com.testpulse.service;

import com.testpulse.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
@Slf4j
public class WelcomeEmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public WelcomeEmailService(JavaMailSender mailSender,
                               @Value("${spring.mail.username}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Async
    public void sendWelcomeEmailAsync(User user, String temporaryPassword) {
        try {
            log.info("Sending welcome email to {}", user.getEmail());
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(user.getEmail());
            helper.setSubject("Welcome to TestPlus — your account is ready");

            String welcomeText = buildWelcomeHtml(user, temporaryPassword);
            helper.setText(buildPlainText(user, temporaryPassword), welcomeText);
            mailSender.send(message);
            log.info("Welcome email sent successfully to {}", user.getEmail());
        } catch (MessagingException ex) {
            log.error("Failed to send welcome email to {}", user.getEmail(), ex);
        }
    }

    private String buildPlainText(User user, String temporaryPassword) {
        return "Hello " + user.getFullName() + ",\n\n"
                + "Welcome to TestPlus! Your account has been created successfully.\n\n"
                + "Temporary password: " + temporaryPassword + "\n\n"
                + "You can use this password for regular login and then change it from your profile anytime.\n\n"
                + "Why TestPlus?\n"
                + "- Practice with smart mock tests\n"
                + "- Track your performance and growth\n"
                + "- Prepare for competitive and professional exams\n"
                + "- Explore curated plans and premium features\n\n"
                + "Start learning today at TestPlus.\n"
                + "Best regards,\n"
                + "The TestPlus Team";
    }

    private String buildWelcomeHtml(User user, String temporaryPassword) {
        String userName = HtmlUtils.htmlEscape(user.getFullName() == null || user.getFullName().isBlank() ? "there" : user.getFullName());
        String password = HtmlUtils.htmlEscape(temporaryPassword);

        return "<div style='margin:0;padding:0;background:#f4f7fb;font-family:Arial,Helvetica,sans-serif;'>"
                + "<table role='presentation' width='100%' cellpadding='0' cellspacing='0' style='background:#f4f7fb;padding:32px 0;'>"
                + "<tr><td align='center'>"
                + "<table role='presentation' width='100%' style='max-width:640px;background:#ffffff;border-radius:18px;overflow:hidden;box-shadow:0 10px 30px rgba(15,23,42,0.08);'>"
                + "<tr><td style='background:linear-gradient(135deg,#1d4ed8,#7c3aed);padding:28px 32px;color:#ffffff;'>"
                + "<h2 style='margin:0;font-size:30px;line-height:1.2;'>Welcome to TestPlus</h2>"
                + "<p style='margin:12px 0 0;font-size:15px;opacity:0.9;'>Your journey to smarter learning starts here.</p>"
                + "</td></tr>"
                + "<tr><td style='padding:32px;'>"
                + "<p style='margin:0 0 18px;font-size:16px;color:#1f2937;'>Hello <strong>" + userName + "</strong>,</p>"
                + "<p style='margin:0 0 22px;font-size:16px;line-height:1.7;color:#374151;'>Your TestPlus account is ready, and you are now part of a faster, smarter way to prepare for your exams and career goals.</p>"
                + "<div style='background:#eef4ff;border:1px solid #dbeafe;border-radius:12px;padding:18px 20px;margin-bottom:24px;'>"
                + "<div style='font-size:12px;letter-spacing:0.08em;text-transform:uppercase;color:#4f46e5;font-weight:700;margin-bottom:8px;'>Temporary Password</div>"
                + "<div style='font-size:28px;font-weight:700;color:#111827;letter-spacing:0.04em;'>" + password + "</div>"
                + "</div>"
                + "<p style='margin:0 0 18px;font-size:15px;line-height:1.7;color:#374151;'>Use this password for standard login. You can change it anytime from your profile settings after signing in.</p>"
                + "<table role='presentation' width='100%' cellpadding='0' cellspacing='0' style='margin:28px 0 18px;'>"
                + "<tr><td style='padding:0 0 14px;'>"
                + "<div style='display:inline-block;padding:12px 18px;border-radius:10px;background:#111827;color:#ffffff;font-weight:700;font-size:14px;'>Why students love TestPlus</div>"
                + "</td></tr>"
                + "<tr><td style='padding:0 0 10px;'><span style='color:#2563eb;font-weight:700;'>✓</span> Smart practice tests and quizzes</td></tr>"
                + "<tr><td style='padding:0 0 10px;'><span style='color:#2563eb;font-weight:700;'>✓</span> Personalized exam preparation insights</td></tr>"
                + "<tr><td style='padding:0 0 10px;'><span style='color:#2563eb;font-weight:700;'>✓</span> Track your scores, progress, and milestones</td></tr>"
                + "<tr><td style='padding:0 0 10px;'><span style='color:#2563eb;font-weight:700;'>✓</span> Discover premium plans designed for growth</td></tr>"
                + "</table>"
                + "<div style='text-align:center;padding-top:18px;'>"
                + "<a href='https://testpluse-production.up.railway.app/' style='display:inline-block;background:linear-gradient(135deg,#2563eb,#7c3aed);color:#ffffff;text-decoration:none;padding:14px 24px;border-radius:999px;font-size:15px;font-weight:700;'>Open TestPlus</a>"
                + "</div>"
                + "</td></tr>"
                + "<tr><td style='padding:0 32px 28px;color:#6b7280;font-size:13px;line-height:1.7;'>"
                + "<p style='margin:0;'>Need help? Contact our support team anytime.</p>"
                + "<p style='margin:10px 0 0;'>Best regards,<br><strong style='color:#111827;'>The TestPlus Team</strong></p>"
                + "</td></tr>"
                + "</table>"
                + "</td></tr>"
                + "</table>"
                + "</div>";
    }
}
