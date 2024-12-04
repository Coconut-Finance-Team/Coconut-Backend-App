package com.coconut.stock_app.controller;

import com.coconut.stock_app.service.AuthenticationService;
import com.coconut.stock_app.dto.auth.UserRegisterRequest;
import com.coconut.stock_app.dto.email.PasswordResetRequest;
import com.coconut.stock_app.dto.user.UserInfoDto;
import com.coconut.stock_app.entity.on_premise.User;
import com.coconut.stock_app.service.EmailService;
import com.coconut.stock_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final EmailService emailService;

    /**
     * 회원가입 API
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegisterRequest request) {
        // 이메일 인증 여부 확인
        if (!emailService.isEmailVerified(request.getEmail())) {
            return ResponseEntity.badRequest().body("이메일 인증이 필요합니다.");
        }

        userService.registerUser(request);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    /**
     * 로그인된 사용자 정보 조회 API
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfoDto> getLoggedInUser() {
        User authenticatedUser = authenticationService.getAuthenticatedUser();

        UserInfoDto userInfo = userService.getUserInfo(authenticatedUser);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 비밀번호 찾기 - 이메일로 임시 비밀번호 전송
     */
    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest request) {
        userService.resetPassword(request.getEmail());
        return ResponseEntity.ok("임시 비밀번호가 이메일로 발송되었습니다.");
    }
}
