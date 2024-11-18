package com.coconut.stock_app.websocket;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockWebSocketHandler extends TextWebSocketHandler {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentHashMap<String, WebSocketSession> stockSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String stockCode = getStockCodeFromSession(session);
        if (stockCode != null) {
            stockSessions.put(stockCode, session);
            System.out.println("종목 연결: " + session.getId() + " (종목 코드: " + stockCode + ")");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String stockCode = getStockCodeFromSession(session);
        if (stockCode != null) {
            stockSessions.remove(stockCode);
            System.out.println("종목 연결 종료: " + session.getId() + " (종목 코드: " + stockCode + ")");
        }
    }

    @Scheduled(fixedRate = 1000)
    public void pushStockData() {
        for (Map.Entry<String, WebSocketSession> entry : stockSessions.entrySet()) {
            String stockCode = entry.getKey();
            WebSocketSession session = entry.getValue();

            if (session.isOpen()) {
                List<String> stockDataList = redisTemplate.opsForList().range("stock-" + stockCode, 0, -1);
                if (stockDataList != null && !stockDataList.isEmpty()) {
                    try {
                        String jsonData = objectMapper.writeValueAsString(stockDataList);
                        session.sendMessage(new TextMessage(jsonData));
                        System.out.println("전송 성공 [" + stockCode + "]: " + jsonData);
                    } catch (IOException e) {
                        System.err.println("전송 실패 [" + stockCode + "]: " + e.getMessage());
                    }
                }
            }
        }
    }

    private String getStockCodeFromSession(WebSocketSession session) {
        String path = session.getUri().getPath();
        if (path != null) {
            return path.substring(path.lastIndexOf("/") + 1);
        }
        return null;
    }
}
