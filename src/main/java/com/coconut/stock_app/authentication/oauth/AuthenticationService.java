package com.coconut.stock_app.authentication.oauth;

import com.coconut.stock_app.entity.on_premise.User;
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
}
