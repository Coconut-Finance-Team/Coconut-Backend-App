package com.coconut.stock_app.service.impl;

import com.coconut.stock_app.dto.stock.StockChartResponse;
import com.coconut.stock_app.entity.cloud.StockChart;
import com.coconut.stock_app.repository.cloud.StockChartRepository;
import com.coconut.stock_app.service.StockChartService;
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
    public List<StockChartResponse> getStockChartData(String stockCode) {
        try {
            log.info("Fetching chart data for stock: {}", stockCode);
            List<StockChart> chartData = stockChartRepository
                    .findByStockStockCodeOrderByTimeAsc(stockCode);

            return chartData.stream()
                    .map(StockChartResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get stock chart data for {}", stockCode, e);
            throw new RuntimeException("Failed to get stock chart data", e);
        }
    }

    @Override
    public List<StockChartResponse> getStockChartDataAfter(String stockCode, String time) {
        try {
            log.info("Fetching chart data for stock: {} after time: {}", stockCode, time);
            List<StockChart> chartData = stockChartRepository
                    .findByStockStockCodeAndTimeGreaterThanOrderByTimeAsc(stockCode, time);

            return chartData.stream()
                    .map(StockChartResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get stock chart data for {} after {}", stockCode, time, e);
            throw new RuntimeException("Failed to get stock chart data", e);
        }
    }
}