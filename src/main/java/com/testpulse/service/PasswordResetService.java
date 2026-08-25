package com.testpulse.service;

import com.testpulse.dto.ForgotPasswordRequest;
import com.testpulse.dto.ResetPasswordRequest;
import com.testpulse.model.PasswordResetOtp;
import com.testpulse.model.User;
import com.testpulse.repository.PasswordResetOtpRepository;
import com.testpulse.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class PasswordResetService {
    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int OTP_COOLDOWN_SECONDS = 60;

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();
    private final String fromAddress;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetOtpRepository otpRepository,
                                JavaMailSender mailSender,
                                PasswordEncoder passwordEncoder,
                                @Value("${spring.mail.username}") String fromAddress) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
        this.fromAddress = fromAddress;
    }

    public void sendOtp(ForgotPasswordRequest request) {
        String email = normalizeEmail(request.getEmail());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No account is registered with this email."));

        otpRepository.findTopByEmailAndUsedAtIsNullOrderByCreatedAtDesc(email).ifPresent(previous -> {
            if (previous.getCreatedAt().plusSeconds(OTP_COOLDOWN_SECONDS).isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("Please wait before requesting another OTP.");
            }
        });

        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
        PasswordResetOtp resetOtp = new PasswordResetOtp();
        resetOtp.setEmail(email);
        resetOtp.setOtpHash(passwordEncoder.encode(otp));
        resetOtp.setCreatedAt(LocalDateTime.now());
        resetOtp.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otpRepository.save(resetOtp);

        sendEmail(user, otp);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String email = normalizeEmail(request.getEmail());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or OTP."));
        PasswordResetOtp resetOtp = otpRepository.findTopByEmailAndUsedAtIsNullOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or OTP."));

        if (resetOtp.getExpiresAt().isBefore(LocalDateTime.now())
                || !passwordEncoder.matches(request.getOtp(), resetOtp.getOtpHash())) {
            throw new IllegalArgumentException("Invalid or expired OTP.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        resetOtp.setUsedAt(LocalDateTime.now());
        otpRepository.save(resetOtp);
    }

    private void sendEmail(User user, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(user.getEmail());
            helper.setSubject("TestPlus password reset OTP");
                String plainText = "Hello " + user.getFullName() + ",\n\n"
                    + "Your TestPlus password reset OTP is: " + otp + "\n"
                    + "It expires in " + OTP_EXPIRY_MINUTES + " minutes.\n\n"
                    + "If you did not request this, ignore this email.";
                String htmlText = "<p>Hello " + HtmlUtils.htmlEscape(user.getFullName()) + ",</p>"
                    + "<p>Your TestPlus password reset OTP is: <strong>" + otp + "</strong></p>"
                    + "<p>It expires in " + OTP_EXPIRY_MINUTES + " minutes.</p>"
                    + "<p>If you did not request this, ignore this email.</p>";
                helper.setText(plainText, htmlText);
            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new IllegalStateException("Unable to send the password reset email.", ex);
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}