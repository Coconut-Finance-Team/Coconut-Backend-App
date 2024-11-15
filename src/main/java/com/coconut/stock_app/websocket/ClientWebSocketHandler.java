package com.coconut.stock_app.websocket;

import java.io.IOException;
import java.util.HashMap;
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

/*
 * WebSocket을 통해 클라이언트와의 실시간 통신을 관리하는 클래스
 * 내부 Redis에 저장된 데이터를 클라이언트에 전송하는 역할
*/
@Component
@RequiredArgsConstructor
public class ClientWebSocketHandler extends TextWebSocketHandler {

    private final RedisTemplate<String, String> redisTemplate;
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

    // 메시지 전송
    public void broadcastMessage(String jsonMessage) {
        sessions.values().forEach(session -> {
            try {
                session.sendMessage(new TextMessage(jsonMessage));
            } catch (IOException e) {
                System.err.println("메시지 전송 실패: " + e.getMessage());
            }
        });
    }

    @Scheduled(fixedRate = 1000) // 1초마다 업데이트
    public void pushStockIndices() {
        Map<String, List<String>> data = new HashMap<>();
        data.put("kospi", redisTemplate.opsForList().range("kospi",0,-1));
        data.put("kosdaq", redisTemplate.opsForList().range("kosdaq",0, -1));

        System.out.println("전송 데이터: " + data);

        for (WebSocketSession session : sessions.values()) {
            try {
                String json = new ObjectMapper().writeValueAsString(data);
                session.sendMessage(new TextMessage(json));
                System.out.println("데이터 전송 성공: " + json);
            } catch (IOException e) {
                System.err.println("데이터 전송 실패: " + e.getMessage());
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception)
            throws Exception {
        System.err.println("WebSocket 오류: " + exception.getMessage());
    }
}
