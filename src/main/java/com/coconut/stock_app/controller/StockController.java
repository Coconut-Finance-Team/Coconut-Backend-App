package com.coconut.stock_app.controller;

import java.net.URISyntaxException;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.coconut.stock_app.service.KISWebSocketClient;
import com.coconut.stock_app.service.StockService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
public class StockController {
    private final KISWebSocketClient kisWebSocketClient;
    private final StockService stockService;

    @GetMapping("/kospi")
    public Map<String, Object> getKOSPIIndex() {
        return stockService.getKOSPIIndex();
    }

    @GetMapping("/kosdaq")
    public Map getKOSDAQIndex() {
        return stockService.getKOSDAQIndex();
    }

    @GetMapping("/subscribe")
    public String subscribeStock() {
        try {
            kisWebSocketClient.connect();
            return "WebSocket 연결 및 종목 구독 성공!";
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "WebSocket 연결 실패: " + e.getMessage();
        }
    }

}
