package com.coconut.stock_app.dto.email;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailVerificationRequest {
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
}