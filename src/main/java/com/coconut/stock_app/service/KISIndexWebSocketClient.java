//package com.coconut.stock_app.service;
//
//import com.coconut.stock_app.config.ApiConfig;
//import lombok.RequiredArgsConstructor;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.handshake.ServerHandshake;
//import org.json.JSONObject;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.concurrent.TimeUnit;
//
//@Service
//@RequiredArgsConstructor
//public class KISIndexWebSocketClient {
//    private WebSocketClient webSocketClient;
//    private boolean isSubscribedKOSPI = false;
//    private boolean isSubscribedKOSDAQ = false;
//
//    private final RedisTemplate<String, Object> redisTemplate;
//    private final ApiConfig apiConfig;
//    private final KISApiService kisApiService;
//
//    public void connect() throws URISyntaxException {
//        String approvalKey = kisApiService.getWebSocketKey();
//        String uri = apiConfig.getStockPriceEndpoint() + "?approval_key=" + approvalKey;
//
//        webSocketClient = new WebSocketClient(new URI(uri)) {
//            @Override
//            public void onOpen(ServerHandshake handshake) {
//                System.out.println("WebSocket 연결 성공: 코스피/코스닥 지수 구독 준비 중...");
//            }
//
//            @Override
//            public void onMessage(String message) {
//                System.out.println("Received Message: " + message);
//
//                if (message.startsWith("{")) {
//                    JSONObject jsonObject = new JSONObject(message);
//
//                    // PINGPONG 메시지 처리
//                    if (jsonObject.has("header") && "PINGPONG".equals(
//                            jsonObject.getJSONObject("header").optString("tr_id"))) {
//                        if (!isSubscribedKOSPI) {
//                            subscribeIndex("0001", approvalKey); // 코스피
//                            isSubscribedKOSPI = true;
//                        }
//                        if (!isSubscribedKOSDAQ) {
//                            subscribeIndex("0002", approvalKey); // 코스닥
//                            isSubscribedKOSDAQ = true;
//                        }
//                        return;
//                    }
//
//                    // 구독 성공 메시지 확인
//                    if (jsonObject.has("header") && "H0UPCNT0".equals(
//                            jsonObject.getJSONObject("header").optString("tr_id"))) {
//                        if ("0".equals(jsonObject.getJSONObject("body").optString("rt_cd"))) {
//                            System.out.println("구독 성공: 실시간 지수 데이터 대기 중...");
//                        }
//                        return;
//                    }
//                }
//
//                // 실시간 지수 데이터 처리
//                if (message.startsWith("0|")) {
//                    processIndexMessage(message);
//                }
//            }
//
//            @Override
//            public void onClose(int code, String reason, boolean remote) {
//                System.out.println("WebSocket 연결 종료: " + reason);
//                isSubscribedKOSPI = false;
//                isSubscribedKOSDAQ = false;
//            }
//
//            @Override
//            public void onError(Exception ex) {
//                System.err.println("WebSocket 오류 발생: " + ex.getMessage());
//            }
//        };
//
//        webSocketClient.connect();
//    }
//
//    private void subscribeIndex(String indexCode, String approvalKey) {
//        JSONObject request = new JSONObject();
//
//        JSONObject header = new JSONObject();
//        header.put("approval_key", approvalKey);
//        header.put("custtype", "P");
//        header.put("tr_type", "1");
//        header.put("content-type", "utf-8");
//
//        JSONObject body = new JSONObject();
//        JSONObject input = new JSONObject();
//        input.put("tr_id", "H0UPCNT0");
//        input.put("tr_key", indexCode);
//        body.put("input", input);
//
//        request.put("header", header);
//        request.put("body", body);
//
//        webSocketClient.send(request.toString());
//        System.out.println("지수 구독 요청 전송: " + request.toString());
//    }
//
//    private void processIndexMessage(String message) {
//        String[] parts = message.split("\\|");
//        if (parts.length < 4) {
//            return;
//        }
//
//        String[] fields = parts[3].split("\\^");
//        if (fields.length < 10) {
//            return;
//        }
//
//        String indexCode = fields[0];
//        BigDecimal currentIndex = new BigDecimal(fields[2]); // 현재 지수
//        BigDecimal changeValue = new BigDecimal(fields[4]); // 등락폭
//        String changeDirection = fields[3]; // 등락 방향 (상승/하락)
//        Long volume = Long.parseLong(fields[5]); // 거래량
//
//        String redisKey = "kospi";
//        if ("0002".equals(indexCode)) {
//            redisKey = "kosdaq";
//        }
//
//        // Redis에 지수 데이터 저장
//        JSONObject indexData = new JSONObject();
//        indexData.put("currentIndex", currentIndex);
//        indexData.put("changeValue", changeValue);
//        indexData.put("changeDirection", changeDirection);
//        indexData.put("volume", volume);
//
//        redisTemplate.opsForValue().set(redisKey, indexData.toString(), 1, TimeUnit.MINUTES);
//        System.out.println("Redis 저장 완료: " + redisKey + " -> " + indexData.toString());
//    }
//}
