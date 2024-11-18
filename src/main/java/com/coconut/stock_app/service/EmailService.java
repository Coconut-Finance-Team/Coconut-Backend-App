package com.coconut.stock_app.service;

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
public class EmailService {

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    // 인증 메일 발송
    public void sendVerificationEmail(String to) {
        try {
            String verificationCode = generateVerificationCode();

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("[Coconut] 이메일 인증");
            message.setText("인증 코드: " + verificationCode + "\n"
                    + "이 코드는 5분간 유효합니다.");

            // Redis에 인증 코드 저장 (5분 유효)
            redisTemplate.opsForValue().set(
                    "EmailVerification:" + to,
                    verificationCode,
                    Duration.ofMinutes(5)
            );

            mailSender.send(message);
            log.info("Verification email sent to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", to, e);
            throw new RuntimeException("이메일 발송 실패", e);
        }
    }

    // 인증 코드 검증
    public boolean verifyEmail(String email, String code) {
        String key = "EmailVerification:" + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            log.warn("Verification code not found or expired for: {}", email);
            return false;
        }

        if (storedCode.equals(code)) {
            redisTemplate.delete(key);
            log.info("Email verified successfully: {}", email);
            return true;
        }

        log.warn("Invalid verification code for: {}", email);
        return false;
    }

    // 6자리 랜덤 코드 생성
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}