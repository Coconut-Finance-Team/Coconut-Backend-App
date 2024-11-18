package com.coconut.stock_app.controller;

import com.coconut.stock_app.dto.auth.JwtResultDto;
import com.coconut.stock_app.dto.auth.LoginDto;
import com.coconut.stock_app.authentication.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class AuthenticationController {

    private static final String LOGIN_SUCCESS_MESSAGE = "로그인 성공!";
    private static final String LOGIN_FAILURE_MESSAGE = "잘못된 로그인 정보입니다.";

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/authenticate")
    public ResponseEntity<JwtResultDto> login(@RequestBody LoginDto loginDto) {
        try {
            // 인증 시도
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getId(), loginDto.getPassword())
            );
        } catch (AuthenticationException e) {
            // 인증 실패 시
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new JwtResultDto(null, LOGIN_FAILURE_MESSAGE));
        }

        // 인증 성공: JWT 생성
        final String jwt = jwtUtil.generateToken(loginDto.getId());

        return ResponseEntity.ok().body(new JwtResultDto(jwt, LOGIN_SUCCESS_MESSAGE));
    }
}
