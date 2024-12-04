package com.coconut.stock_app.authentication.oauth;

import com.coconut.stock_app.authentication.jwt.JwtUtil;
import com.coconut.stock_app.entity.on_premise.User;
import com.coconut.stock_app.repository.on_premise.UserRepository;
import com.coconut.stock_app.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private final EmailService emailService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // OAuth2 로그인한 이메일은 자동으로 인증 처리
        emailService.setGoogleEmailVerified(email);

        Optional<User> userOptional = userRepository.findByEmail(email);

        String redirectUrl;
        if (userOptional.isPresent()) {
            String jwt = jwtUtil.generateToken(userOptional.get().getId());
            redirectUrl = frontendUrl + "/?token=" + jwt;
        } else {
            redirectUrl = frontendUrl + "/signin?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                    + "&name=" + URLEncoder.encode(name, StandardCharsets.UTF_8)
                    + "&googleId=" + URLEncoder.encode(oAuth2User.getAttribute("sub"), StandardCharsets.UTF_8);
        }

        response.sendRedirect(redirectUrl);
    }
}