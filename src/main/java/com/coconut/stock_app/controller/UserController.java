package com.coconut.stock_app.controller;

import com.coconut.stock_app.dto.auth.UserRegisterRequest;
import com.coconut.stock_app.entity.on_premise.User;
import com.coconut.stock_app.entity.on_premise.UserAccountStatus;
import com.coconut.stock_app.entity.on_premise.UserRole;
import com.coconut.stock_app.repository.on_premise.UserRepository;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegisterRequest request) {
        // 비밀번호와 비밀번호 확인 검증
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        // User 엔티티 생성 및 저장
        User user = User.builder()
                .userUuid(UUID.randomUUID().toString())
                .id(request.getId()) // 사용자 ID 설정
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt로 비밀번호 인코딩
                .gender(request.getGender())
                .job(request.getJob())
                .investmentStyle(request.getInvestmentStyle())
                .birthdate(LocalDate.parse(request.getBirthdate())) // 생년월일 변환
                .phone(request.getPhone())
                .socialSecurityNumber(request.getSocialSecurityNumber())
                .accountStatus(UserAccountStatus.ACTIVE)
                .role(UserRole.USER)
                .build();

        userRepository.save(user);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }
}
