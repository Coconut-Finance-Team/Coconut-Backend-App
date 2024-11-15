package com.coconut.stock_app.authentication.oauth;

import java.util.Collections;
import java.util.Map;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coconut.stock_app.repository.on_premise.UserRepository;
import lombok.RequiredArgsConstructor;

// OAuth 로그인
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 사용자 정보 추출 (Google의 경우)
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // 인증된 OAuth2User 객체 반환
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "email" // 이메일을 고유 식별자로 설정
        );
    }
}
