package com.coconut.stock_app.authentication.oauth;

import com.coconut.stock_app.authentication.jwt.JwtUtil;
import com.coconut.stock_app.entity.on_premise.User;
import com.coconut.stock_app.repository.on_premise.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // OAuth2 사용자 정보 가져오기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // 이메일로 사용자 조회
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            // 기존 사용자: JWT 생성 및 메인 페이지로 리디렉션
            String jwt = jwtUtil.generateToken(email);
            response.sendRedirect(frontendUrl + "/?token=" + jwt);
        } else {
            // 신규 사용자: 회원가입 페이지로 리디렉션
            response.sendRedirect(frontendUrl + "/signin");
        }
    }
}
