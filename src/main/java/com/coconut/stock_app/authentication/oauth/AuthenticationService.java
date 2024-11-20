package com.coconut.stock_app.authentication.oauth;

import com.coconut.stock_app.entity.on_premise.User;
import com.coconut.stock_app.entity.on_premise.UserRole;
import com.coconut.stock_app.repository.on_premise.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    /**
     * 현재 요청의 JWT에서 인증된 사용자 조회
     */
    public User getAuthenticatedUser() {
        // 현재 SecurityContext에서 Authentication 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 객체가 없거나 익명 사용자일 경우 예외 발생
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UsernameNotFoundException("인증된 사용자가 아닙니다.");
        }

        // Principal에서 이메일 정보 추출
        String email = authentication.getName(); // 보통 UserDetails의 username 반환

        // 이메일로 사용자 조회
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
    }

    public boolean isAuthenticatedAdmin() {
        // 현재 인증된 사용자 가져오기
        User user = getAuthenticatedUser();

        // 사용자 역할 확인 (예: "ROLE_ADMIN" 체크)
        return user.getRole() == UserRole.ADMIN;
    }

    /**
     * 인증된 관리자인지 확인하고 예외를 던지는 메서드
     */
    public void validateAdminAccess() {
        if (!isAuthenticatedAdmin()) {
            throw new SecurityException("접근 권한이 없습니다. 관리자만 접근 가능합니다.");
        }
    }
}
