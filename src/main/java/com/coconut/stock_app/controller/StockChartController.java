package com.coconut.stock_app.controller;

import com.coconut.stock_app.dto.stock.StockChartResponse;
import com.coconut.stock_app.dto.stock.StockSearchResponse;
import com.coconut.stock_app.service.StockChartService;
import com.coconut.stock_app.service.StockSearchService;
import com.coconut.stock_app.websocket.BatchStorage;
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
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
@Slf4j
public class StockChartController {

    private final StockChartService stockChartService;
    private final StockSearchService stockSearchService;
    private final BatchStorage batchStorage;

    // 기존 1분, 10분, 60분 차트 API
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

    @GetMapping("/search")
    public ResponseEntity<List<StockSearchResponse>> searchStocks(@RequestParam String keyword) {
        try {
            List<StockSearchResponse> results = stockSearchService.searchStocks(keyword);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Failed to search stocks with keyword: {}", keyword, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 공통 상세 데이터 로직
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

