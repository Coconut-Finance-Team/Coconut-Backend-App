package com.coconut.stock_app.service.impl;

import com.coconut.stock_app.service.EmailService;
import java.time.Duration;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String EMAIL_VERIFICATION_PREFIX = "EmailVerification:";
    private static final String EMAIL_VERIFICATION_STATUS_PREFIX = "EmailVerified:";
    private static final int VERIFICATION_CODE_LENGTH = 6;
    private static final int CODE_EXPIRATION_MINUTES = 5;

    @Override
    public void sendVerificationEmail(String to) {
        try {
            String verificationCode = generateVerificationCode();
            sendEmail(to, verificationCode);
            saveVerificationCode(to, verificationCode);
            log.info("Verification email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", to, e);
            throw new RuntimeException("이메일 발송 실패", e);
        }
    }

    @Override
    public boolean verifyEmail(String email, String code) {
        String key = getVerificationKey(email);
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            log.warn("Verification code not found or expired for: {}", email);
            return false;
        }

        if (storedCode.equals(code)) {
            redisTemplate.delete(key);
            setVerifiedStatus(email); // 이메일 인증 상태 저장
            log.info("Email verified successfully: {}", email);
            return true;
        }

        log.warn("Invalid verification code for: {}", email);
        return false;
    }

    @Override
    public boolean isEmailVerified(String email) {
        String verifiedKey = getVerifiedKey(email);
        return redisTemplate.opsForValue().get(verifiedKey) != null;
    }

    /**
     * 임시 비밀번호 전송
     */
    @Override
    public void sendTemporaryPassword(String to, String temporaryPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("[Coconut] 임시 비밀번호 발급");
            message.setText(createTemporaryPasswordEmailContent(temporaryPassword));
            mailSender.send(message);
            log.info("Temporary password email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send temporary password email to: {}", to, e);
            throw new RuntimeException("이메일 발송 실패", e);
        }
    }

    private String createTemporaryPasswordEmailContent(String temporaryPassword) {
        return String.format("임시 비밀번호: %s\n해당 비밀번호로 로그인 후 비밀번호를 반드시 변경해주세요.", temporaryPassword);
    }

    private void sendEmail(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[Coconut] 이메일 인증");
        message.setText(createEmailContent(verificationCode));
        mailSender.send(message);
    }

    private void saveVerificationCode(String email, String code) {
        redisTemplate.opsForValue().set(
                getVerificationKey(email),
                code,
                Duration.ofMinutes(CODE_EXPIRATION_MINUTES)
        );
    }

    private void setVerifiedStatus(String email) {
        redisTemplate.opsForValue().set(
                getVerifiedKey(email),
                "true",
                Duration.ofHours(1) // 이메일 인증 상태 유효 시간
        );
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%0" + VERIFICATION_CODE_LENGTH + "d",
                random.nextInt((int) Math.pow(10, VERIFICATION_CODE_LENGTH)));
    }

    private String createEmailContent(String verificationCode) {
        return String.format("인증 코드: %s\n이 코드는 %d분간 유효합니다.",
                verificationCode, CODE_EXPIRATION_MINUTES);
    }

    private String getVerificationKey(String email) {
        return EMAIL_VERIFICATION_PREFIX + email;
    }

    private String getVerifiedKey(String email) {
        return EMAIL_VERIFICATION_STATUS_PREFIX + email;
    }
}