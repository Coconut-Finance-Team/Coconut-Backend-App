package com.coconut.stock_app.controller;

import com.coconut.stock_app.dto.stock.StockChartResponse;
import com.coconut.stock_app.service.StockChartService;
import com.coconut.stock_app.websocket.BatchStorage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
@Slf4j
public class StockChartController {

    private final BatchStorage batchStorage;
    private final StockChartService stockChartService;

    @GetMapping("/{stockCode}/charts/1min")
    public ResponseEntity<List<StockChartResponse>> getStockChartData1Min(@PathVariable String stockCode) {
        return getStockChartResponse(stockCode, "1min");
    }

    @GetMapping("/{stockCode}/charts/10min")
    public ResponseEntity<List<StockChartResponse>> getStockChartData10Min(@PathVariable String stockCode) {
        return getStockChartResponse(stockCode, "10min");
    }

    @GetMapping("/{stockCode}/charts/60min")
    public ResponseEntity<List<StockChartResponse>> getStockChartData60Min(@PathVariable String stockCode) {
        return getStockChartResponse(stockCode, "60min");
    }

    private ResponseEntity<List<StockChartResponse>> getStockChartResponse(String stockCode, String interval) {
        try {
            batchStorage.saveBatchToDatabase(); // 주식 데이터 저장 로직 호출
            List<StockChartResponse> response = stockChartService.getStockChartData(stockCode, interval);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get stock chart data for interval: {}", interval, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
