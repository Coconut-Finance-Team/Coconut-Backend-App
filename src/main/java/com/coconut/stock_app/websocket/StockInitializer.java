package com.coconut.stock_app.websocket;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import com.coconut.stock_app.entity.cloud.Stock;
import com.coconut.stock_app.entity.cloud.StockStatus;
import com.coconut.stock_app.repository.cloud.StockRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockInitializer {

    private final StockRepository stockRepository;

    //@PostConstruct
    public void initializeStocks() {
        List<Stock> initialStocks = Arrays.asList(
                Stock.builder()
                        .stockCode("005930")
                        .stockName("삼성전자")
                        .exchangeCode("KOSPI")
                        .stockStatus(StockStatus.ACTIVE)
                        .listedDate(LocalDate.of(1975, 6, 11))  // 실제 상장일
                        .stockCharts(new ArrayList<>())
                        .build(),
                Stock.builder()
                        .stockCode("066570")
                        .stockName("LG전자")
                        .exchangeCode("KOSPI")
                        .stockStatus(StockStatus.ACTIVE)
                        .listedDate(LocalDate.of(2002, 1, 1))
                        .stockCharts(new ArrayList<>())
                        .build(),
                Stock.builder()
                        .stockCode("000660")
                        .stockName("SK하이닉스")
                        .exchangeCode("KOSPI")
                        .stockStatus(StockStatus.ACTIVE)
                        .listedDate(LocalDate.of(1996, 12, 26))
                        .stockCharts(new ArrayList<>())
                        .build()
        );

        // 존재하지 않는 종목만 저장
        for (Stock stock : initialStocks) {
            Optional<Stock> existingStock = stockRepository.findByStockCode(stock.getStockCode());
            if (!existingStock.isPresent()) {
                log.info("새로운 종목 등록: {}", stock.getStockName());
                stockRepository.save(stock);
            }
        }
    }
}