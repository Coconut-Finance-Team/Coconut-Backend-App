package com.coconut.stock_app.controller;

import com.coconut.stock_app.dto.ipo.IPODTO;
import com.coconut.stock_app.entity.cloud.IPO;
import com.coconut.stock_app.service.IPOService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class IPOController {
    private final IPOService ipoService;

    @GetMapping("/ipo/active")
    public ResponseEntity<List<IPODTO>> getActiveIPOs() {
        return ResponseEntity.ok(ipoService.getActiveIPOs());
    }
}
