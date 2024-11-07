package com.coconut.stock_app.controller;

import com.coconut.stock_app.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @GetMapping("/kospi")
    public Map<String, Object> getKOSPIIndex() {
        return stockService.getKOSPIIndex();
    }

    @GetMapping("/kosdaq")
    public Map getKOSDAQIndex() {
        return stockService.getKOSDAQIndex();
    }
}
