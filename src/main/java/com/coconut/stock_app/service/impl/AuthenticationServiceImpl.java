package com.coconut.stock_app.service.impl;

import com.coconut.stock_app.entity.on_premise.User;
import com.coconut.stock_app.entity.on_premise.UserAccountStatus;
import com.coconut.stock_app.entity.on_premise.UserRole;
import com.coconut.stock_app.exception.CustomException;
import com.coconut.stock_app.exception.ErrorCode;
import com.coconut.stock_app.repository.on_premise.UserRepository;
import com.coconut.stock_app.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    @Override
    public User getAuthenticatedUser() {
        // 현재 SecurityContext에서 Authentication 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 객체가 없거나 익명 사용자일 경우 예외 발생
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal()
                .equals("anonymousUser")) {
            throw new UsernameNotFoundException("인증된 사용자가 아닙니다.");
        }

        // Principal에서 ID 또는 이메일 정보 추출
        String identifier = authentication.getName(); // UserDetails의 username 반환
        User user;

        // ID로 조회 시도
        user = userRepository.findById(identifier).orElse(null);
        if (user == null) {
            // 이메일로 조회 시도
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + identifier));
        }

        if (user.getAccountStatus() == UserAccountStatus.SUSPENDED) {
            throw new CustomException(ErrorCode.SUSPEND_USER);
        }

        return user;
    }

    @Override
    public boolean isAuthenticatedAdmin() {
        // 현재 인증된 사용자 가져오기
        User user = getAuthenticatedUser();

        // 사용자 역할 확인 (예: "ROLE_ADMIN" 체크)
        return user.getRole() == UserRole.ADMIN;
    }

    @Override
    public void validateAdminAccess() {
        if (!isAuthenticatedAdmin()) {
            throw new SecurityException("접근 권한이 없습니다. 관리자만 접근 가능합니다.");
        }
    }
}
