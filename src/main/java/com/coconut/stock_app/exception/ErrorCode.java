package com.coconut.stock_app.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NOT_EXIST_USER(HttpStatus.NOT_FOUND,"유저를 찾을 수 없습니다."),
    NOT_EXIST_STOCK(HttpStatus.NOT_FOUND,"종목을 찾을 수 없습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN,"권한이 없습니다"),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED,"인증이 필요한 유저입니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");

    private final HttpStatus httpStatus;
    private final String msg;
}
