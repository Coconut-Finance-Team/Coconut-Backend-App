package com.coconut.stock_app.service.impl;

import com.coconut.stock_app.service.AuthenticationService;
import com.coconut.stock_app.dto.auth.UserRegisterRequest;
import com.coconut.stock_app.dto.user.UserInfoDto;
import com.coconut.stock_app.entity.on_premise.User;
import com.coconut.stock_app.entity.on_premise.UserAccountStatus;
import com.coconut.stock_app.entity.on_premise.UserRole;
import com.coconut.stock_app.repository.on_premise.UserRepository;
import com.coconut.stock_app.service.EmailService;
import com.coconut.stock_app.service.UserService;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    private final EmailService emailService;

    @Override
    public void registerUser(UserRegisterRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 확인
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        // 사용자 생성 및 저장
        User user = User.builder()
                .userUuid(UUID.randomUUID().toString())
                .id(request.getId())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .job(request.getJob())
                .investmentStyle(request.getInvestmentStyle())
                .birthdate(LocalDate.parse(request.getBirthdate()))
                .phone(request.getPhone())
                .socialSecurityNumber(request.getSocialSecurityNumber())
                .accountStatus(UserAccountStatus.ACTIVE)
                .role(UserRole.USER)
                .build();

        userRepository.save(user);
    }

    @Override
    public UserInfoDto getUserInfo(User user) {
        // 필요한 사용자 정보만 DTO로 변환
        return UserInfoDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.getGender())
                .job(user.getJob())
                .investmentStyle(user.getInvestmentStyle())
                .birthdate(
                        user.getBirthdate() != null ? user.getBirthdate().toString() : null) // LocalDate를 String으로 변환
                .primaryAccountId(user.getPrimaryAccount() != null ? user.getPrimaryAccount().getAccountId() : null)
                .build();
    }

    @Override
    public User verifyUser(String username, String phone, String socialSecurityNumber) {
        // 이름, 전화번호, 주민등록번호로 사용자 조회
        return userRepository.findByUsernameAndPhoneAndSocialSecurityNumber(username, phone, socialSecurityNumber)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 일치하지 않습니다."));
    }

    @Override
    public void resetPassword(String email) {
        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        // 임시 비밀번호 생성
        String temporaryPassword = generateTemporaryPassword();

        // 비밀번호 변경
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        userRepository.save(user);

        // 이메일로 임시 비밀번호 전송
        emailService.sendTemporaryPassword(email, temporaryPassword);
    }

    private String generateTemporaryPassword() {
        int length = 8;
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$";
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charSet.length());
            password.append(charSet.charAt(index));
        }
        return password.toString();
    }
}
