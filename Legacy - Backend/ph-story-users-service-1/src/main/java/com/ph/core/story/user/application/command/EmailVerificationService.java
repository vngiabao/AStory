package com.ph.core.story.user.application.command;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ph.core.story.common.exception.BusinessValidationException;
import com.ph.core.story.common.exception.ResourceNotFoundException;
import com.ph.core.story.user.application.command.dto.EmailOtpStatusResponse;
import com.ph.core.story.user.application.command.dto.VerifyEmailOtpRequest;
import com.ph.core.story.user.domain.model.User;
import com.ph.core.story.user.domain.repository.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationProperties properties;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public EmailOtpStatusResponse sendOtp(Long userId) {
        User user = findUser(userId);

        if (user.isEmailVerified()) {
            throw new BusinessValidationException("Email already verified", user.getEmail());
        }

        Instant now = Instant.now();
        long resendAfterSeconds = secondsUntilNextSend(user, now);
        if (resendAfterSeconds > 0) {
            throw new BusinessValidationException("Please wait before requesting another OTP",
                    resendAfterSeconds);
        }

        String otp = generateOtp();

        // Buoc 1: hash OTP roi moi luu DB, giong cach luu password.
        user.setEmailVerificationOtpHash(passwordEncoder.encode(otp));
        user.setEmailVerificationOtpExpiresAt(now.plus(Duration.ofMinutes(properties.getOtpTtlMinutes())));
        user.setEmailVerificationOtpSentAt(now);
        user.setEmailVerificationFailedAttempts(0);

        userRepository.save(user);

        // Buoc 2: gui OTP qua email da dang ky. Local/dev co the tat sendEmail de log ma.
        deliverOtp(user.getEmail(), otp);

        return new EmailOtpStatusResponse(user.getEmail(),
                Duration.ofMinutes(properties.getOtpTtlMinutes()).toSeconds(),
                properties.getResendCooldownSeconds());
    }

    @Transactional
    public void verifyOtp(Long userId, VerifyEmailOtpRequest request) {
        User user = findUser(userId);

        if (user.isEmailVerified()) {
            return;
        }

        if (user.getEmailVerificationOtpHash() == null
                || user.getEmailVerificationOtpExpiresAt() == null) {
            throw new BusinessValidationException("Please request an OTP first", null);
        }

        if (Instant.now().isAfter(user.getEmailVerificationOtpExpiresAt())) {
            clearOtp(user);
            userRepository.save(user);
            throw new BusinessValidationException("OTP expired. Please request a new one", null);
        }

        if (user.getEmailVerificationFailedAttempts() >= properties.getMaxFailedAttempts()) {
            clearOtp(user);
            userRepository.save(user);
            throw new BusinessValidationException("Too many incorrect OTP attempts. Please request a new one",
                    null);
        }

        if (!passwordEncoder.matches(request.otp(), user.getEmailVerificationOtpHash())) {
            user.setEmailVerificationFailedAttempts(user.getEmailVerificationFailedAttempts() + 1);
            userRepository.save(user);
            throw new BusinessValidationException("OTP is invalid", null);
        }

        // Buoc 3: dung OTP -> danh dau account da xac thuc va xoa OTP cu.
        user.setEmailVerified(true);
        clearOtp(user);
        userRepository.save(user);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", userId));
    }

    private long secondsUntilNextSend(User user, Instant now) {
        if (user.getEmailVerificationOtpSentAt() == null) {
            return 0;
        }

        Instant nextAllowedSend = user.getEmailVerificationOtpSentAt()
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

    private void deliverOtp(String email, String otp) {
        if (!properties.isSendEmail()) {
            log.info("Email verification OTP for {} is {}", email, otp);
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
        message.setText("Your verification code is " + otp
                + ". This code expires in " + properties.getOtpTtlMinutes() + " minutes.");
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            log.warn("Could not send email verification OTP to {}", email, ex);
            throw new BusinessValidationException("Could not send OTP email. Please try again later", null);
        }
    }

    private void clearOtp(User user) {
        user.setEmailVerificationOtpHash(null);
        user.setEmailVerificationOtpExpiresAt(null);
        user.setEmailVerificationOtpSentAt(null);
        user.setEmailVerificationFailedAttempts(0);
    }
}
