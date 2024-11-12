package com.coconut.stock_app.service;

import java.net.URI;
import java.net.URISyntaxException;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import com.coconut.stock_app.config.ApiConfig;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KISWebSocketClient {
    private WebSocketClient webSocketClient;
    private final ApiConfig apiConfig;
    private final KISApiService kisApiService;

    public void connect() throws URISyntaxException {
        // WebSocket 접속키 가져오기
        String approvalKey = kisApiService.getWebSocketKey();
        String uri = apiConfig.getStockPriceEndpoint() + "?approval_key=" + approvalKey;

        webSocketClient = new WebSocketClient(new URI(uri)) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("WebSocket 연결 성공!");
                // PINGPONG 메시지 확인 후 구독 요청
                new Thread(() -> {
                    try {
                        Thread.sleep(2000); // 잠시 대기 후 구독 요청 전송
                        subscribeStock("005930", approvalKey);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            @Override
            public void onMessage(String message) {
                System.out.println("Received Message: " + message);

                // 1. JSON 형식인지 확인
                if (message.startsWith("{")) {
                    try {
                        // JSON 형식으로 파싱
                        JSONObject jsonObject = new JSONObject(message);

                        // PINGPONG 메시지 무시
                        if (jsonObject.has("header") && "PINGPONG".equals(jsonObject.getJSONObject("header").optString("tr_id"))) {
                            return;
                        }
                    } catch (Exception e) {
                        System.err.println("JSON 파싱 오류 발생: " + e.getMessage());
                    }
                }


            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("WebSocket 연결 종료: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                System.err.println("WebSocket 오류 발생: " + ex.getMessage());
            }
        };

        webSocketClient.connect();
    }

    // 종목 구독 요청 메서드 수정
    private void subscribeStock(String stockCode, String approvalKey) {
        // 요청 JSON 생성
        JSONObject request = new JSONObject();

        // Header 구성
        JSONObject header = new JSONObject();
        header.put("approval_key", approvalKey);
        header.put("custtype", "P");
        header.put("tr_type", "1");
        header.put("content-type", "utf-8");

        // Body 구성
        JSONObject body = new JSONObject();
        JSONObject input = new JSONObject();
        input.put("tr_id", "H0STCNT0");
        input.put("tr_key", stockCode);
        body.put("input", input);

        // 전체 요청 JSON 조립
        request.put("header", header);
        request.put("body", body);

        // WebSocket으로 메시지 전송
        webSocketClient.send(request.toString());
        System.out.println("종목 시세 구독 요청 전송: " + request.toString());
    }
}
