package com.coconut.stock_app.controller;


import com.coconut.stock_app.service.AuthenticationService;
import com.coconut.stock_app.dto.admin.UserInfoDetailForAdminDTO;
import com.coconut.stock_app.dto.admin.UserInfoForAdminDto;
import com.coconut.stock_app.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/read/user/{uuid}")
    public ResponseEntity<UserInfoDetailForAdminDTO> getUser(@PathVariable String uuid) {
        authenticationService.validateAdminAccess();
        return ResponseEntity.ok(adminService.getUser(uuid));
    }

    @PatchMapping("/suspend/user/{uuid}")
    public ResponseEntity<Void> suspendUser(@PathVariable String uuid) {
        authenticationService.validateAdminAccess();
        adminService.suspendUser(uuid);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/resume/user/{uuid}")
    public ResponseEntity<Void> resumeUser(@PathVariable String uuid) {
        authenticationService.validateAdminAccess();
        adminService.resumeUser(uuid);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/suspend/account/{uuid}")
    public ResponseEntity<Void> suspendAccount(@PathVariable String uuid) {
        authenticationService.validateAdminAccess();
        adminService.suspendAccount(uuid);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/resume/account/{uuid}")
    public ResponseEntity<Void> resumeAccount(@PathVariable String uuid) {
        authenticationService.validateAdminAccess();
        adminService.resumeAccount(uuid);
        return ResponseEntity.ok().build();
    }
}
