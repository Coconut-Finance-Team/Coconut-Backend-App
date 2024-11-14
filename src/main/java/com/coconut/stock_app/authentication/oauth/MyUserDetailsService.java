package com.coconut.stock_app.authentication.oauth;

import com.coconut.stock_app.entity.on_premise.User;
import com.coconut.stock_app.repository.on_premise.UserRepository;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + id));

        return new org.springframework.security.core.userdetails.User(
                user.getId(),
                user.getPassword() != null ? user.getPassword() : "", // 비밀번호가 없는 경우 빈 문자열
                new ArrayList<>() // 권한 리스트 (예: "ROLE_USER")
        );
    }
}
