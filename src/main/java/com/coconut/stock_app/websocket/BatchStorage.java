package com.coconut.stock_app.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.coconut.stock_app.entity.cloud.StockChart;
import com.coconut.stock_app.repository.cloud.StockChartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BatchStorage {

    private final StockChartRepository stockChartRepository;
    private final ConcurrentMap<String, List<StockChart>> stockDataMap = new ConcurrentHashMap<>();

    public void addStockData(String stockCode, StockChart data) {
        stockDataMap.computeIfAbsent(stockCode, k -> new ArrayList<>()).add(data);
    }

    public ConcurrentMap<String, List<StockChart>> getAllStockData() {
        return stockDataMap;
    }

    public void clearStockData(String stockCode) {
        stockDataMap.remove(stockCode);
    }

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void saveBatchToDatabase() {
        try {
            // BatchStorage의 모든 데이터 가져오기
            ConcurrentMap<String, List<StockChart>> stockDataMap = getAllStockData();

            for (String stockCode : stockDataMap.keySet()) {
                List<StockChart> stockCharts = stockDataMap.get(stockCode);

                if (stockCharts != null && !stockCharts.isEmpty()) {
                    // MySQL로 데이터 저장
                    stockChartRepository.saveAll(new ArrayList<>(stockCharts));
                    log.info("MySQL 저장 완료 - 종목: {}, 저장 데이터 수: {}", stockCode, stockCharts.size());

                    // BatchStorage에서 해당 종목 데이터 제거
                    clearStockData(stockCode);
                }
            }
        } catch (Exception e) {
            log.error("Batch 저장 중 오류 발생", e);
        }
    }
}

