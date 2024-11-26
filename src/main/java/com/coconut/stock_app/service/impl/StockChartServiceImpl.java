package com.coconut.stock_app.service.impl;

import com.coconut.stock_app.dto.stock.StockChartResponse;
import com.coconut.stock_app.entity.cloud.StockChart;
import com.coconut.stock_app.repository.cloud.StockChartRepository;
import com.coconut.stock_app.service.StockChartService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockChartServiceImpl implements StockChartService {

    private final StockChartRepository stockChartRepository;

    @Override
    public List<StockChartResponse> getStockChartData(String stockCode, String timeInterval) {
        try {
            // 데이터 범위를 계산
            LocalDateTime startTime = switch (timeInterval) {
                case "1min" -> LocalDateTime.now().minusMinutes(40);
                case "10min" -> LocalDateTime.now().minusMinutes(400);
                case "60min" -> LocalDateTime.now().minusMinutes(2400);
                default -> throw new IllegalArgumentException("Invalid interval: " + timeInterval);
            };

            log.debug("Fetching stock data for stockCode: {}, interval: {}, startTime: {}", stockCode, timeInterval, startTime);

            // Repository에서 데이터 조회
            List<StockChart> chartData = stockChartRepository.findByStockCodeAndTimeRange(stockCode, startTime);
            return chartData.stream()
                    .map(StockChartResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get stock chart data for {} interval", timeInterval, e);
            throw new RuntimeException("Failed to get stock chart data", e);
        }
    }
}
