//package com.coconut.stock_app.service;
//
//import com.coconut.stock_app.config.ApiConfig;
//import com.coconut.stock_app.entity.cloud.Stock;
//import com.coconut.stock_app.entity.cloud.StockChart;
//import com.coconut.stock_app.repository.cloud.StockChartRepository;
//import com.coconut.stock_app.repository.cloud.StockRepository;
//import lombok.RequiredArgsConstructor;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.handshake.ServerHandshake;
//import org.json.JSONObject;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.time.LocalDateTime;
//
//import static java.lang.Thread.sleep;
//
//@Service
//@RequiredArgsConstructor
//public class KISWebSocketClient {
//    private WebSocketClient webSocketClient;
//    private boolean isSubscribed = false;
//
//    private final StockChartRepository stockChartRepository;
//    private final StockRepository stockRepository;
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
//                System.out.println("WebSocket 연결 성공!");
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
//                    if (jsonObject.has("header") && "PINGPONG".equals(jsonObject.getJSONObject("header").optString("tr_id"))) {
//                        if (!isSubscribed) {
//                            subscribeStock("005930", kisApiService.getWebSocketKey());
//                            isSubscribed = true;
//                        }
//                        return;
//                    }
//
//                    // 구독 성공 메시지 확인
//                    if (jsonObject.has("header") && "H0STCNT0".equals(jsonObject.getJSONObject("header").optString("tr_id"))) {
//                        if ("0".equals(jsonObject.getJSONObject("body").optString("rt_cd"))) {
//                            System.out.println("구독 성공: 실시간 시세 데이터 대기 중...");
//                        }
//                    }
//                }
//
//                // 실시간 시세 데이터 처리
//                if (message.startsWith("0|")) {
//                    processStockMessage(message);
//                }
//            }
//
//            @Override
//            public void onClose(int code, String reason, boolean remote) {
//                System.out.println("WebSocket 연결 종료: " + reason);
//                isSubscribed = false;
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
//    private void subscribeStock(String stockCode, String approvalKey) {
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
//        input.put("tr_id", "H0STCNT0");
//        input.put("tr_key", stockCode);
//        body.put("input", input);
//
//        request.put("header", header);
//        request.put("body", body);
//
//        webSocketClient.send(request.toString());
//        System.out.println("종목 시세 구독 요청 전송: " + request.toString());
//    }
//
//    private void processStockMessage(String message) {
//        String[] parts = message.split("\\|");
//        if (parts.length < 4) return;
//
//        String[] fields = parts[3].split("\\^");
//        if (fields.length < 18) return;
//
//        String stockCode = fields[0];
//        BigDecimal openPrice = new BigDecimal(fields[2]);
//        BigDecimal highPrice = new BigDecimal(fields[7]);
//        BigDecimal lowPrice = new BigDecimal(fields[8]);
//        BigDecimal closePrice = new BigDecimal(fields[9]);
//        Long volume = Long.parseLong(fields[12]);
//        LocalDateTime tradeTime = LocalDateTime.now();
//
//        Stock stock = stockRepository.findByStockCode(stockCode);
//
//        StockChart stockChart = StockChart.builder()
//                .stock(stock)
//                .tradeDate(tradeTime.toLocalDate())
//                .openPrice(openPrice)
//                .highPrice(highPrice)
//                .lowPrice(lowPrice)
//                .closePrice(closePrice)
//                .volume(volume)
//                .build();
//
//        stockChartRepository.save(stockChart);
//    }
//}
