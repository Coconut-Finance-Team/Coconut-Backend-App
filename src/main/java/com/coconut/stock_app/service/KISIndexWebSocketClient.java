package com.coconut.stock_app.service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import com.coconut.stock_app.dto.StockIndexDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KISIndexWebSocketClient {

    private final RedisTemplate<String, StockIndexDto> redisTemplate;
    private final KISApiService kisApiService;

    private WebSocketSession session;
    private volatile boolean isSubscribedKOSPI = false; // 동기화를 위한 volatile
    private volatile boolean isSubscribedKOSDAQ = false;

    @PostConstruct
    public void initializeWebSocketConnection() {
        CompletableFuture.runAsync(() -> {
            try {
                System.out.println("[StockIndexWebSocketService] Initializing WebSocket connection...");
                connectToWebSocket();
                System.out.println("[StockIndexWebSocketService] WebSocket 연결 작업 완료.");
            } catch (Exception e) {
                System.err.println("[StockIndexWebSocketService] WebSocket 초기화 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void connectToWebSocket() {
        WebSocketClient client = new StandardWebSocketClient();

        try {
            String wsUrl = "ws://ops.koreainvestment.com:21000/tryitout/H0UPCNT0";

            client.execute(new AbstractWebSocketHandler() {
                @Override
                public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                    KISIndexWebSocketClient.this.session = session;
                    System.out.println("[StockIndexWebSocketService] WebSocket 연결 성공");

                    // approval_key 갱신 및 초기 핸드셰이크 전송
                    sendHandshakeMessage();
                }

                @Override
                public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                    String payload = message.getPayload();
                    System.out.println("[DEBUG] Raw WebSocket Response: " + payload);

                    if (payload.contains("\"tr_id\":\"PINGPONG\"")) {
                        handlePingPongMessage();
                        return;
                    }

                    if (payload.contains("\"msg1\":\"SUBSCRIBE SUCCESS\"")) {
                        handleSubscribeSuccess(payload);
                        return;
                    }

                    handleReceivedMessage(payload);
                }

                @Override
                public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                    System.err.println("[StockIndexWebSocketService] WebSocket 오류 발생: " + exception.getMessage());
                    reconnectWithBackoff();
                }

                @Override
                public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
                    System.out.println("[StockIndexWebSocketService] WebSocket 연결 종료: " + status);
                    reconnectWithBackoff();
                }
            }, wsUrl);

        } catch (Exception e) {
            System.err.println("[StockIndexWebSocketService] WebSocket 연결 실패: " + e.getMessage());
            reconnectWithBackoff();
        }
    }

    private synchronized void sendHandshakeMessage() throws IOException {
        kisApiService.invalidateApprovalKey();
        String approvalKey = kisApiService.getApprovalKey();

        String handshakeMessage = String.format(
                "{\"header\":{\"approval_key\":\"%s\",\"custtype\":\"P\",\"tr_type\":\"1\",\"content-type\":\"utf-8\"},\"body\":{\"input\":{\"tr_id\":\"H0UPCNT0\",\"tr_key\":\"H0UPCNT0\"}}}",
                approvalKey);

        session.sendMessage(new TextMessage(handshakeMessage));
        System.out.println("[StockIndexWebSocketService] 핸드셰이크 메시지 전송: " + handshakeMessage);
    }

    private synchronized void handlePingPongMessage() {
        try {
            if (!isSubscribedKOSPI) {
                sendSubscribeMessage("0001");
                isSubscribedKOSPI = true;
            }
            if (!isSubscribedKOSDAQ) {
                sendSubscribeMessage("0002");
                isSubscribedKOSDAQ = true;
            }
            session.sendMessage(new TextMessage("{\"tr_id\":\"PONG\"}"));
            System.out.println("[StockIndexWebSocketService] PONG 메시지 전송 완료");
        } catch (Exception e) {
            System.err.println("[StockIndexWebSocketService] PINGPONG 처리 중 오류: " + e.getMessage());
        }
    }

    private void handleSubscribeSuccess(String payload) {
        if (payload.contains("\"tr_key\":\"0001\"")) {
            isSubscribedKOSPI = true;
            System.out.println("[DEBUG] KOSPI 구독 성공");
        } else if (payload.contains("\"tr_key\":\"0002\"")) {
            isSubscribedKOSDAQ = true;
            System.out.println("[DEBUG] KOSDAQ 구독 성공");
        }
    }

    private void sendSubscribeMessage(String trKey) throws IOException {
        String approvalKey = kisApiService.getApprovalKey();

        if (session == null || !session.isOpen()) {
            System.err.println("[StockIndexWebSocketService] WebSocket 세션이 열려있지 않습니다. 구독 요청 실패");
            kisApiService.invalidateApprovalKey();
            return;
        }

        String subscribeMessage = String.format(
                "{\"header\": {\"approval_key\": \"%s\", \"custtype\": \"P\", \"tr_type\": \"1\", \"content-type\": \"utf-8\"}, \"body\": {\"input\": {\"tr_id\": \"H0UPCNT0\", \"tr_key\": \"%s\"}}}",
                approvalKey, trKey);

        session.sendMessage(new TextMessage(subscribeMessage));
        System.out.println("[StockIndexWebSocketService] 구독 요청 전송: tr_key=" + trKey);
    }

    private void handleReceivedMessage(String payload) {
        try {
            String[] parts = payload.split("\\|");
            if (parts.length < 4) {
                System.err.println("[StockIndexWebSocketService] 수신 데이터가 잘못되었습니다: " + payload);
                return;
            }

            String data = parts[3];
            String[] values = data.split("\\^");

            if ("0001".equals(values[0])) {
                processStockData("kospi", "KOSPI", values);
            } else if ("0002".equals(values[0])) {
                processStockData("kosdaq", "KOSDAQ", values);
            }
        } catch (Exception e) {
            System.err.println("[StockIndexWebSocketService] 수신 메시지 처리 중 오류: " + e.getMessage());
        }
    }

    private void processStockData(String redisKey, String marketName, String[] values) {
        try {
            StockIndexDto stockData = new StockIndexDto(values[0], // marketCode
                    values[1], // marketTime
                    values[2], // currentIndex
                    values[3], // changeSign
                    values[4], // changeValue
                    values[5], // cumulativeVolume
                    values[6], // cumulativeAmount
                    values[7], // perTradeVolume
                    values[8], // perTradeAmount
                    values[9], // previousDayRate
                    values[10], // openingIndex
                    values[11], // highIndex
                    values[12], // lowIndex
                    values[13], // openVsCurrent
                    values[14], // openVsSign
                    values[15], // highVsCurrent
                    values[16], // highVsSign
                    values[17], // lowVsCurrent
                    values[18], // lowVsSign
                    values[19], // prevCloseVsOpenRate
                    values[20], // prevCloseVsHighRate
                    values[21], // prevCloseVsLowRate
                    values[22], // upperLimitCount
                    values[23], // risingCount
                    values[24], // unchangedCount
                    values[25], // fallingCount
                    values[26], // lowerLimitCount
                    values[27], // strongRisingCount
                    values[28], // strongFallingCount
                    values[29] // tickDifference
            );
            redisTemplate.opsForValue().set(redisKey, stockData, 1, TimeUnit.MINUTES);
            System.out.println("[StockIndexWebSocketService] " + marketName + " 데이터 저장: " + stockData);
        } catch (Exception e) {
            System.err.println("[StockIndexWebSocketService] " + marketName + " 데이터 처리 오류: " + e.getMessage());
        }
    }

    private void reconnectWithBackoff() {
        int backoff = 1000;
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(backoff);
                connectToWebSocket();
                return;
            } catch (InterruptedException e) {
                System.err.println("[StockIndexWebSocketService] 재연결 대기 중 오류: " + e.getMessage());
            }
            backoff *= 2;
        }
        System.err.println("[StockIndexWebSocketService] WebSocket 재연결 실패: 최대 시도 초과");
    }
}
