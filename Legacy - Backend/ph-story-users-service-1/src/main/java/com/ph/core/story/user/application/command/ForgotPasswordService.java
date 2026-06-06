package com.ph.core.story.user.application.command;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ph.core.story.common.exception.BusinessValidationException;
import com.ph.core.story.user.application.command.dto.ForgotPasswordOtpStatusResponse;
import com.ph.core.story.user.application.command.dto.ForgotPasswordResetRequest;
import com.ph.core.story.user.application.command.dto.ForgotPasswordSendOtpRequest;
import com.ph.core.story.user.application.command.dto.ForgotPasswordVerifyOtpRequest;
import com.ph.core.story.user.application.command.dto.ForgotPasswordVerifyOtpResponse;
import com.ph.core.story.user.domain.model.User;
import com.ph.core.story.user.domain.repository.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private static final String INVALID_OTP_MESSAGE = "OTP is invalid or has expired";
    private static final String INVALID_RESET_TOKEN_MESSAGE =
            "Reset session is invalid or has expired";

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetProperties properties;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final JdbcTemplate jdbcTemplate;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public ForgotPasswordOtpStatusResponse sendOtp(ForgotPasswordSendOtpRequest request) {
        String normalizedEmail = request.email().trim();
        Instant now = Instant.now();
        long resendAfterSeconds = properties.getResendCooldownSeconds();

        userRepository.findByEmail(normalizedEmail).ifPresent(user -> {
            long secondsUntilNextSend = secondsUntilNextSend(user, now);
            if (secondsUntilNextSend > 0) {
                return;
            }

            String otp = generateOtp();
            user.setPasswordResetOtpHash(passwordEncoder.encode(otp));
            user.setPasswordResetOtpExpiresAt(now.plus(Duration.ofMinutes(properties.getOtpTtlMinutes())));
            user.setPasswordResetOtpSentAt(now);
            user.setPasswordResetFailedAttempts(0);
            user.setPasswordResetTokenHash(null);
            user.setPasswordResetTokenExpiresAt(null);
            userRepository.save(user);

            deliverOtp(normalizedEmail, otp);
        });

        return new ForgotPasswordOtpStatusResponse(normalizedEmail,
                Duration.ofMinutes(properties.getOtpTtlMinutes()).toSeconds(),
                resendAfterSeconds);
    }

    @Transactional
    public ForgotPasswordVerifyOtpResponse verifyOtp(ForgotPasswordVerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.email().trim())
                .orElseThrow(() -> new BusinessValidationException(INVALID_OTP_MESSAGE, null));

        if (user.getPasswordResetOtpHash() == null || user.getPasswordResetOtpExpiresAt() == null) {
            throw new BusinessValidationException(INVALID_OTP_MESSAGE, null);
        }

        if (Instant.now().isAfter(user.getPasswordResetOtpExpiresAt())) {
            clearPasswordResetOtp(user);
            userRepository.save(user);
            throw new BusinessValidationException(INVALID_OTP_MESSAGE, null);
        }

        if (user.getPasswordResetFailedAttempts() >= properties.getMaxFailedAttempts()) {
            clearPasswordResetOtp(user);
            userRepository.save(user);
            throw new BusinessValidationException(INVALID_OTP_MESSAGE, null);
        }

        if (!passwordEncoder.matches(request.otp(), user.getPasswordResetOtpHash())) {
            user.setPasswordResetFailedAttempts(user.getPasswordResetFailedAttempts() + 1);
            userRepository.save(user);
            throw new BusinessValidationException(INVALID_OTP_MESSAGE, null);
        }

        String resetToken = generateResetToken();
        user.setPasswordResetTokenHash(passwordEncoder.encode(resetToken));
        user.setPasswordResetTokenExpiresAt(
                Instant.now().plus(Duration.ofMinutes(properties.getResetTokenTtlMinutes())));
        clearPasswordResetOtp(user);
        userRepository.save(user);

        return new ForgotPasswordVerifyOtpResponse(resetToken,
                Duration.ofMinutes(properties.getResetTokenTtlMinutes()).toSeconds());
    }

    @Transactional
    public void resetPassword(ForgotPasswordResetRequest request) {
        User user = userRepository.findByEmail(request.email().trim())
                .orElseThrow(() -> new BusinessValidationException(INVALID_RESET_TOKEN_MESSAGE, null));

        if (user.getPasswordResetTokenHash() == null || user.getPasswordResetTokenExpiresAt() == null) {
            throw new BusinessValidationException(INVALID_RESET_TOKEN_MESSAGE, null);
        }

        if (Instant.now().isAfter(user.getPasswordResetTokenExpiresAt())) {
            clearPasswordResetToken(user);
            userRepository.save(user);
            throw new BusinessValidationException(INVALID_RESET_TOKEN_MESSAGE, null);
        }

        if (!passwordEncoder.matches(request.resetToken(), user.getPasswordResetTokenHash())) {
            throw new BusinessValidationException(INVALID_RESET_TOKEN_MESSAGE, null);
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw new BusinessValidationException("New password must be different!", null);
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setFailedLoginAttempts(0);
        user.setAccountNonLocked(true);
        user.setLockTime(null);
        clearPasswordResetOtp(user);
        clearPasswordResetToken(user);
        userRepository.save(user);

        revokeOauth2Authorizations(user.getUsername());
    }

    private long secondsUntilNextSend(User user, Instant now) {
        if (user.getPasswordResetOtpSentAt() == null) {
            return 0;
        }

        Instant nextAllowedSend = user.getPasswordResetOtpSentAt()
                .plusSeconds(properties.getResendCooldownSeconds());
        if (!now.isBefore(nextAllowedSend)) {
            return 0;
        }

        return Duration.between(now, nextAllowedSend).toSeconds();
    }

    private String generateOtp() {
        int bound = (int) Math.pow(10, properties.getOtpLength());
        int floor = bound / 10;
        return String.valueOf(floor + secureRandom.nextInt(bound - floor));
    }

    private String generateResetToken() {
        return UUID.randomUUID() + "-" + UUID.randomUUID();
    }

    private void deliverOtp(String email, String otp) {
        if (!properties.isSendEmail()) {
            log.info("Password reset OTP for {} is {}", email, otp);
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new BusinessValidationException("Email service is not configured", null);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.getFrom());
        message.setTo(email);
        message.setSubject(properties.getSubject());
        message.setText("Your password reset code is " + otp
                + ". This code expires in " + properties.getOtpTtlMinutes() + " minutes.");
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            log.warn("Could not send password reset OTP to {}", email, ex);
            throw new BusinessValidationException("Could not send OTP email. Please try again later",
                    null);
        }
    }

    private void revokeOauth2Authorizations(String username) {
        jdbcTemplate.update("DELETE FROM oauth2_authorization WHERE principal_name = ?", username);
    }

    private void clearPasswordResetOtp(User user) {
        user.setPasswordResetOtpHash(null);
        user.setPasswordResetOtpExpiresAt(null);
        user.setPasswordResetOtpSentAt(null);
        user.setPasswordResetFailedAttempts(0);
    }

    private void clearPasswordResetToken(User user) {
        user.setPasswordResetTokenHash(null);
        user.setPasswordResetTokenExpiresAt(null);
    }
}
