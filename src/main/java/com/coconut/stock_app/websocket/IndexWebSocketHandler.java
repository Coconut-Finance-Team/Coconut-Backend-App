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
public class IndexWebSocketHandler extends TextWebSocketHandler {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        System.out.println("클라이언트 연결: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        System.out.println("클라이언트 연결 종료: " + session.getId());
    }

    //@Scheduled(fixedRate = 1000) // 1초마다 Redis 데이터 푸시
    public void pushStockIndices() {
        try {
            Map<String, List<String>> data = Map.of(
                    "kospi", redisTemplate.opsForList().range("kospi", 0, -1),
                    "kosdaq", redisTemplate.opsForList().range("kosdaq", 0, -1)
            );

            // JSON 변환
            String json = objectMapper.writeValueAsString(data);

            // 모든 클라이언트에 전송
            broadcastMessage(json);
            System.out.println("데이터 전송 성공: " + json);
        } catch (Exception e) {
            System.err.println("Redis 데이터 전송 중 오류: " + e.getMessage());
        }
    }

    public void broadcastMessage(String jsonMessage) {
        sessions.values().forEach(session -> {
            if (session.isOpen()) { // 세션 상태 확인
                try {
                    session.sendMessage(new TextMessage(jsonMessage));
                } catch (IOException e) {
                    System.err.println("메시지 전송 실패: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("WebSocket 오류: " + exception.getMessage());
    }
}
