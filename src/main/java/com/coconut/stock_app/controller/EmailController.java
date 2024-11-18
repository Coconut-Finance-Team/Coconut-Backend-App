package com.coconut.stock_app.controller;

import com.coconut.stock_app.dto.email.EmailVerificationRequest;
import com.coconut.stock_app.dto.email.EmailVerificationResponse;
import com.coconut.stock_app.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/verify/send")
    public ResponseEntity<EmailVerificationResponse> sendVerificationEmail(
            @RequestBody @Valid EmailVerificationRequest request) {
        try {
            emailService.sendVerificationEmail(request.getEmail());
            return ResponseEntity.ok(new EmailVerificationResponse(
                    true, "인증 메일이 발송되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new EmailVerificationResponse(
                    false, "메일 발송에 실패했습니다."
            ));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<EmailVerificationResponse> verifyEmail(
            @RequestParam String email,
            @RequestParam String code) {
        boolean isVerified = emailService.verifyEmail(email, code);

        if (isVerified) {
            return ResponseEntity.ok(new EmailVerificationResponse(
                    true, "이메일 인증이 완료되었습니다."
            ));
        } else {
            return ResponseEntity.badRequest().body(new EmailVerificationResponse(
                    false, "잘못된 인증 코드이거나 만료되었습니다."
            ));
        }
    }
}
