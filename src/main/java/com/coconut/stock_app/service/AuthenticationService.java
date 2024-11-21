package com.coconut.stock_app.service;

import com.coconut.stock_app.entity.on_premise.User;

public interface AuthenticationService {

    /**
     * 현재 요청의 JWT에서 인증된 사용자 조회
     */
    User getAuthenticatedUser();

    /**
     * 인증된 관리자인지 여부 확인
     */
    boolean isAuthenticatedAdmin();

    /**
     * 인증된 관리자인지 확인하고 예외를 던지는 메서드
     */
    void validateAdminAccess();
}
