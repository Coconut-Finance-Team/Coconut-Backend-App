package com.coconut.stock_app.controller;

import com.coconut.stock_app.dto.trade.OrderDTO;
import com.coconut.stock_app.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class TradeController {
    private final TradeService tradeService;

    @PostMapping("/buy-order")
    public ResponseEntity<Void> buyOrder(@RequestBody OrderDTO orderDTO) {
        tradeService.processBuyOrder(orderDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sell-order")
    public ResponseEntity<Void> sellOrder(@RequestBody OrderDTO orderDTO) {
        tradeService.processSellOrder(orderDTO);
        return ResponseEntity.ok().build();
    }
}
