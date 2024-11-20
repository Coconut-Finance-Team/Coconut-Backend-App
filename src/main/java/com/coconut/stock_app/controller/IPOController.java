package com.coconut.stock_app.controller;

import com.coconut.stock_app.authentication.oauth.AuthenticationService;
import com.coconut.stock_app.dto.ipo.IPODTO;
import com.coconut.stock_app.dto.ipo.IPORequestDTO;
import com.coconut.stock_app.entity.cloud.IPO;
import com.coconut.stock_app.entity.on_premise.Account;
import com.coconut.stock_app.entity.on_premise.User;
import com.coconut.stock_app.service.IPOService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class IPOController {
    private final IPOService ipoService;
    private final AuthenticationService authenticationService;

    @GetMapping("/ipo/active")
    public ResponseEntity<List<IPODTO>> getActiveIPOs() {
        return ResponseEntity.ok(ipoService.getActiveIPOs());
    }

    @PostMapping("/ipo/subscription")
    public ResponseEntity<Void> subscriptionIPO(@RequestBody IPORequestDTO ipRequestDTO){
        User authenticatedUser = authenticationService.getAuthenticatedUser();
        Account account = authenticatedUser.getPrimaryAccount();
        ipoService.subscriptionIPO(ipRequestDTO, account);
        return ResponseEntity.ok().build();
    }

}
