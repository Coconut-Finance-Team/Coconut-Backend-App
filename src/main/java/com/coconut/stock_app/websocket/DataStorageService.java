package com.coconut.stock_app.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coconut.stock_app.dto.stock.StockChartDTO;
import com.coconut.stock_app.dto.stock.StockIndexDto;
import com.coconut.stock_app.entity.cloud.Stock;
import com.coconut.stock_app.entity.cloud.StockChart;
import com.coconut.stock_app.repository.cloud.StockChartRepository;
import com.coconut.stock_app.repository.cloud.StockRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataStorageService {

    private final RedisTemplate<String, String> redisTemplate;
    private final StockRepository stockRepository;
    private final StockChartRepository stockChartRepository;
    private final ObjectMapper objectMapper;

    public void saveStockData(String stockCode, StockChartDTO data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForList().rightPush("stock-" + stockCode, json);
        } catch (Exception e) {
            log.error("Redis 저장 실패: {}", stockCode, e);
        }
    }

    public void saveIndexData(String market, StockIndexDto data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForList().leftPush(market, json);
        } catch (Exception e) {
            log.error("Redis 저장 실패: {}", market, e);
        }
    }

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void transferToDatabase() {
        try {
            Set<String> keys = redisTemplate.keys("stock-*");
            if (keys == null || keys.isEmpty())
                return;

            List<StockChart> stockCharts = new ArrayList<>();

            for (String key : keys) {
                String stockCode = key.substring(6);
                Optional<Stock> stockOptional = stockRepository.findByStockCode(stockCode);
                if (stockOptional.isEmpty())
                    continue;

                List<String> dataList = redisTemplate.opsForList().range(key, 0, -1);
                if (dataList == null || dataList.isEmpty())
                    continue;

                Stock stock = stockOptional.get();
                for (String json : dataList) {
                    StockChartDTO dto = objectMapper.readValue(json, StockChartDTO.class);
                    stockCharts.add(dto.toEntity(stock));
                }

                redisTemplate.delete(key);
            }

            if (!stockCharts.isEmpty()) {
                stockChartRepository.saveAll(stockCharts);
                log.info("MySQL 저장 완료: {} 건", stockCharts.size());
            }
        } catch (Exception e) {
            log.error("데이터베이스 전송 실패", e);
        }
    }
}
