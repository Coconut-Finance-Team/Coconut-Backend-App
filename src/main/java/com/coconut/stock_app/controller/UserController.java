package com.coconut.stock_app.controller;

import com.coconut.stock_app.authentication.oauth.AuthenticationService;
import com.coconut.stock_app.dto.auth.UserRegisterRequest;
import com.coconut.stock_app.dto.user.UserInfoDto;
import com.coconut.stock_app.entity.on_premise.User;
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

    /**
     * 회원가입 API
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegisterRequest request) {
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
}
