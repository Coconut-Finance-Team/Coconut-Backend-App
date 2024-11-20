package com.coconut.stock_app.controller;


import com.coconut.stock_app.authentication.oauth.AuthenticationService;
import com.coconut.stock_app.dto.user.UserInfoDto;
import com.coconut.stock_app.dto.user.UserInfoForAdminDto;
import com.coconut.stock_app.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin")
public class AdminController {

    private final AuthenticationService authenticationService;
    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<Void> getDashboard() {
        authenticationService.validateAdminAccess();
        return null;
    }

    @GetMapping("/read/user/all")
    public ResponseEntity<List<UserInfoForAdminDto>> getAllUsers() {
        authenticationService.validateAdminAccess();
        return ResponseEntity.ok(adminService.getAllUser());
    }
}
