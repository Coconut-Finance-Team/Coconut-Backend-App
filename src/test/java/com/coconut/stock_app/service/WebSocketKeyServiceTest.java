//package com.coconut.stock_app.service;
//
//import com.coconut.stock_app.config.ApiConfig;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@SpringBootTest
//public class WebSocketKeyServiceTest {
//
//    @Autowired
//    private KISApiService KISApiService;
//
//    @Autowired
//    private ApiConfig apiConfig;
//
//    @Test
//    public void testGetWebSocketKey() {
//        // apiConfig에서 설정한 URL을 가져와 확인합니다.
//        String apiUrl = apiConfig.getRestapiUrl();
//
//        // webSocketKeyService의 메서드를 호출하여 키를 가져옵니다.
//        String webSocketKey = KISApiService.getWebSocketKey();
//
//        // WebSocket 접근키 검증
//        assertNotNull(webSocketKey, "WebSocket 접근키를 받아오지 못했습니다.");
//        System.out.println("WebSocket Key: " + webSocketKey);
//    }
//}
