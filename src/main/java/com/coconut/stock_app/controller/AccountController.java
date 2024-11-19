package com.coconut.stock_app.controller;

import com.coconut.stock_app.dto.account.AccountCreationRequest;
import com.coconut.stock_app.dto.account.AccountCreationResponse;
import com.coconut.stock_app.service.AccountService;
import com.coconut.stock_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;
    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<AccountCreationResponse> createAccount(@RequestBody AccountCreationRequest request) {
        AccountCreationResponse response = accountService.createAccount(request);
        return ResponseEntity.ok(response);
    }
}
