package com.coconut.stock_app.controller;

import com.coconut.stock_app.dto.stock.StockChartResponse;
import com.coconut.stock_app.service.StockChartService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
@Slf4j
public class StockChartController {

    private final StockChartService stockChartService;

    @GetMapping("/{stockCode}/charts")
    public ResponseEntity<List<StockChartResponse>> getStockChartData(
            @PathVariable String stockCode,
            @RequestParam(required = false) String fromTime) {
        try {
            List<StockChartResponse> response;
            if (fromTime != null) {
                response = stockChartService.getStockChartDataAfter(stockCode, fromTime);
            } else {
                response = stockChartService.getStockChartData(stockCode);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get stock chart data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}