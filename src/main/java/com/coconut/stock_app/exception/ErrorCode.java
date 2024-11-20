package com.coconut.stock_app.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NOT_EXIST_USER(HttpStatus.NOT_FOUND,"유저를 찾을 수 없습니다."),
    NOT_EXIST_STOCK(HttpStatus.NOT_FOUND,"종목을 찾을 수 없습니다."),
    NOT_EXIST_ACCOUNT(HttpStatus.NOT_FOUND,"계좌를 찾을 수 없습니다."),
    NOT_EXIST_TRADE(HttpStatus.NOT_FOUND,"체결된 거래를 찾을 수 없습니다."),
    NOT_EXIST_ORDER(HttpStatus.NOT_FOUND,"주문을 찾을 수 없습니다."),
    NOT_EXIST_IPO(HttpStatus.NOT_FOUND,"공모주를 찾을 수 없습니다."),
    NOT_EXIST_PROFIT_LOSS(HttpStatus.NOT_FOUND,"판매 수익을 찾을 수 없습니다."),
    NOT_EXIST_TRANSACTION(HttpStatus.NOT_FOUND,"입출금 기록을 찾을 수 없습니다."),
    NOT_EXIST_OWNED_STOCK(HttpStatus.NOT_FOUND,"보유 종목을 찾을 수 없습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN,"권한이 없습니다"),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED,"인증이 필요한 유저입니다."),
    INSUFFICIENT_FUNDS(HttpStatus.BAD_REQUEST, "금액이 부족합니다."),
    MAX_IPO_REQUEST_EXCEEDED(HttpStatus.BAD_REQUEST, "최대 청약 가능 수량을 초과했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");

    private final HttpStatus httpStatus;
    private final String msg;
}
