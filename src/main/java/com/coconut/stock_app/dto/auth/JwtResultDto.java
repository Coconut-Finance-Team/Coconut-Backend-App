package com.coconut.stock_app.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtResultDto {
    private final String token;   // 발급된 JWT
    private final String message; // 응답 메시지
}
