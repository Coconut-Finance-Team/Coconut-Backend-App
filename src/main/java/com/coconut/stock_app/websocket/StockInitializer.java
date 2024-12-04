package com.coconut.stock_app.websocket;

import com.coconut.stock_app.entity.cloud.Stock;
import com.coconut.stock_app.entity.cloud.StockStatus;
import com.coconut.stock_app.entity.elasticsearch.StockDocument;
import com.coconut.stock_app.repository.cloud.StockRepository;
import com.coconut.stock_app.repository.elasticsearch.StockSearchRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockInitializer {

    private final StockRepository stockRepository;
    private final StockSearchRepository stockSearchRepository;

    @PostConstruct
    private void initializeStocks() {
        List<Stock> initialStocks = Arrays.asList(
                Stock.builder()
                        .stockCode("005930")
                        .stockName("삼성전자")
                        .exchangeCode("KOSPI")
                        .stockStatus(StockStatus.ACTIVE)
                        .listedDate(LocalDate.of(1975, 6, 11))
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
                        .build(),
                Stock.builder()
                        .stockCode("0001")
                        .stockName("KOSPI")
                        .exchangeCode("KOSPI")
                        .stockStatus(StockStatus.ACTIVE)
                        .listedDate(LocalDate.of(1996, 12, 26))
                        .stockCharts(new ArrayList<>())
                        .build(),
                Stock.builder()
                        .stockCode("1001")
                        .stockName("KOSDAQ")
                        .exchangeCode("KOSDAQ")
                        .stockStatus(StockStatus.ACTIVE)
                        .listedDate(LocalDate.of(1996, 12, 26))
                        .stockCharts(new ArrayList<>())
                        .build()
        );

        for (Stock stock : initialStocks) {
            Optional<Stock> existingStock = stockRepository.findByStockCode(stock.getStockCode());
            if (existingStock.isEmpty()) {
                log.info("새로운 종목 등록: {}", stock.getStockName());
                Stock savedStock = stockRepository.save(stock);

                // ElasticSearch에 색인 추가
                indexStockInElasticsearch(savedStock);
            }
        }
    }

    private void indexStockInElasticsearch(Stock stock) {
        try {
            StockDocument document = StockDocument.builder()
                    .stockCode(stock.getStockCode())
                    .stockName(stock.getStockName())
                    .build();

            stockSearchRepository.save(document);
            log.info("ElasticSearch에 색인 완료: {}", stock.getStockName());
        } catch (Exception e) {
            log.error("ElasticSearch 색인 실패: {}", stock.getStockName(), e);
        }
    }
}
