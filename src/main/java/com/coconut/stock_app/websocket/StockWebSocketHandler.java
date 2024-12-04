package com.coconut.stock_app.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@Slf4j
public class StockWebSocketHandler extends TextWebSocketHandler {

    // 종목별 WebSocket 세션 관리
    private final ConcurrentHashMap<String, WebSocketSession> stockSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String stockCode = getStockCodeFromSession(session);
        if (stockCode != null) {
            stockSessions.put(stockCode, session);
            log.info("WebSocket connected: {} (Stock Code: {})", session.getId(), stockCode);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String stockCode = getStockCodeFromSession(session);
        if (stockCode != null) {
            stockSessions.remove(stockCode);
            log.info("WebSocket connection closed: {} (Stock Code: {})", session.getId(), stockCode);
        }
    }

    /**
     * Redis Pub/Sub에서 수신한 데이터를 WebSocket 세션으로 전송
     */
    public void sendStockData(String stockCode, String jsonData) {
        WebSocketSession session = stockSessions.get(stockCode);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(jsonData));
                log.info("Sent data to WebSocket (Stock Code: {}): {}", stockCode, jsonData);
            } catch (IOException e) {
                log.error("Failed to send data to WebSocket (Stock Code: {}): {}", stockCode, e.getMessage());
            }
        } else {
            log.warn("No active WebSocket session for stock code: {}", stockCode);
        }
    }

    /**
     * Extract stock code from WebSocket session URI
     */
    private String getStockCodeFromSession(WebSocketSession session) {
        String path = session.getUri().getPath();
        if (path != null) {
            return path.substring(path.lastIndexOf("/") + 1);
        }
        return null;
    }
}
