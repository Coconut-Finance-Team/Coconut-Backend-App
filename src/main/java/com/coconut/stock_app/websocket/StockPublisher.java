package com.coconut.stock_app.websocket;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.coconut.stock_app.dto.stock.StockChartDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockPublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public void saveStockData(String stockCode, StockChartDTO data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            String channel = "stock-" + stockCode; // 종목별 채널 이름
            redisTemplate.convertAndSend(channel, json); // 채널로 데이터 발행
            log.info("Published to channel: {}", channel);
        } catch (Exception e) {
            log.error("Pub/Sub 데이터 발행 실패: {}", stockCode, e);
        }
    }

}
