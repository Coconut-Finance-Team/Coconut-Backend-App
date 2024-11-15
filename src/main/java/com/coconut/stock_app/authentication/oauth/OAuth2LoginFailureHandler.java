package com.coconut.stock_app.authentication.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        // 실패 시 React의 로그인 페이지로 리디렉션
        response.sendRedirect(frontendUrl + "/login?error=true");
    }
}
