package com.coconut.stock_app.websocket;

import com.coconut.stock_app.dto.stock.StockChartDTO;
import com.coconut.stock_app.entity.cloud.Stock;
import com.coconut.stock_app.entity.cloud.StockChart;
import com.coconut.stock_app.repository.cloud.StockChartRepository;
import com.coconut.stock_app.repository.cloud.StockRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockDataSubscriber implements MessageListener {
    private final StockWebSocketHandler stockWebSocketHandler;
    private final BatchStorage batchStorage;
    private final ObjectMapper objectMapper;
    private final StockChartRepository stockChartRepository;
    private final StockRepository stockRepository;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel()); // 채널 이름
        String json = new String(message.getBody()); // 수신 데이터

        if (!channel.startsWith("stock-")) return;

        String stockCode = channel.substring(6); // stock-{종목코드}에서 stockCode 추출

        try {
            // WebSocket으로 데이터 전송
            stockWebSocketHandler.sendStockData(stockCode, json);

            // MySQL에 저장할 데이터를 BatchStorage에 추가
            Optional<Stock> stockOptional = stockRepository.findByStockCode(stockCode);
            if (stockOptional.isPresent()) {
                Stock stock = stockOptional.get();
                StockChartDTO dto = objectMapper.readValue(json, StockChartDTO.class);
                batchStorage.addStockData(stockCode, dto.toEntity(stock));
            }
        } catch (Exception e) {
            log.error("Failed to process stock data from channel: {}", channel, e);
        }
    }

    private void handleStockData(String stockCode, String json) throws Exception {
        Optional<Stock> stockOptional = stockRepository.findByStockCode(stockCode);
        if (stockOptional.isEmpty()) {
            log.warn("미등록 종목: {}", stockCode);
            return;
        }

        Stock stock = stockOptional.get();
        StockChartDTO dto = objectMapper.readValue(json, StockChartDTO.class);
        StockChart stockChart = dto.toEntity(stock);

        stockChartRepository.save(stockChart); // MySQL 저장
        log.info("MySQL 저장 완료 - 종목: {}", stockCode);
    }

}
