package com.coconut.stock_app.websocket;

import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketManager {

    private final StockDataHandler stockDataHandler;
    private final KISKeyService kisKeyService;
    private final ThreadPoolTaskExecutor webSocketExecutor;

    @Value("${websocket.url:ws://ops.koreainvestment.com:21000}")
    private String websocketUrl;

    @Value("${websocket.retry.max-attempts:5}")
    private int maxRetryAttempts;

    @Value("${websocket.retry.initial-delay:1000}")
    private long initialDelay;

    private WebSocketSession session;
    private final AtomicBoolean isConnected = new AtomicBoolean(false);

    private static final String[][] SUBSCRIPTIONS = {{"H0UPCNT0", "0001", "KOSPI"},
            {"H0UPCNT0", "0002", "KOSDAQ"}, {"H0STCNT0", "005930", "삼성전자"},
            {"H0STCNT0", "066570", "LG전자"}, {"H0STCNT0", "000660", "SK하이닉스"}};

    @PostConstruct
    public void initialize() {
        webSocketExecutor.execute(this::connectWithRetry);
    }

    private void connectWithRetry() {
        int retryCount = 0;
        while (!isConnected.get() && retryCount < maxRetryAttempts) {
            try {
                WebSocketClient client = new StandardWebSocketClient();
                client.execute(createWebSocketHandler(), websocketUrl);
                return;
            } catch (Exception e) {
                retryCount++;
                sleep(calculateBackoff(retryCount));
            }
        }
    }

    private WebSocketHandler createWebSocketHandler() {
        return new AbstractWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession newSession) {
                session = newSession;
                isConnected.set(true);
                log.info("WebSocket 연결 성공");
                sendSubscriptions(); // 구독 신청
                startConnectionMonitor(); // 연결 상태 모니터링 시작
            }

            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) {
                stockDataHandler.handleMessage(message.getPayload());
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
                handleDisconnect("Connection closed: " + status);
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) {
                handleDisconnect("Transport error: " + exception.getMessage());
            }
        };
    }

    private void handleDisconnect(String reason) {
        log.warn("WebSocket 연결 종료: {}", reason);
        isConnected.set(false);
        session = null;
        webSocketExecutor.execute(this::connectWithRetry);
    }

    private void sendSubscriptions() {
        try {
            String approvalKey = kisKeyService.getApprovalKey();
            for (String[] subscription : SUBSCRIPTIONS) {
                String message = String.format(
                        "{\"header\":{\"approval_key\":\"%s\",\"custtype\":\"P\",\"tr_type\":\"1\",\"content-type\":\"utf-8\"},"
                                + "\"body\":{\"input\":{\"tr_id\":\"%s\",\"tr_key\":\"%s\"}}}",
                        approvalKey, subscription[0], subscription[1]);
                session.sendMessage(new TextMessage(message));
                log.info("구독 요청 전송: {}", subscription[2]);
                Thread.sleep(100); // 각 요청 사이에 100ms 지연 추가
            }
        } catch (Exception e) {
            log.error("구독 요청 실패", e);
            handleDisconnect("Subscription failed");
        }
    }

    private void startConnectionMonitor() {
        webSocketExecutor.execute(() -> {
            while (isConnected.get()) {
                try {
                    if (session == null || !session.isOpen()) {
                        handleDisconnect("Connection check failed");
                        break;
                    }
                    Thread.sleep(5000); // 5초마다 연결 체크
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    private long calculateBackoff(int retryCount) {
        return Math.min(initialDelay * (1L << (retryCount - 1)), 30000);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
