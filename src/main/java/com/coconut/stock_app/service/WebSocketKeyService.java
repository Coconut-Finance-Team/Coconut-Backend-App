// package com.coconut.stock_app.service;

// import java.time.LocalDateTime;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.UUID;
// import org.json.JSONException;
// import org.json.JSONObject;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;
// import com.coconut.stock_app.config.ApiConfig;
// import jakarta.annotation.PostConstruct;
// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class WebSocketKeyService {

//     private final ApiConfig apiConfig;
//     private final RestTemplate restTemplate;

//     private String webSocketKey;
//     private LocalDateTime keyExpirationTime;

//     // 최초 실행 시 WebSocket 키를 받아옴
//     @PostConstruct
//     public void initializeWebSocketKey() {
//         this.webSocketKey = fetchWebSocketKey();
//         this.keyExpirationTime = LocalDateTime.now().plusHours(24); // 키 유효 시간 설정 (예시: 24시간)
//     }

//     // WebSocket 키 가져오기
//     public String getWebSocketKey() {
//         try {
//             // JSON 응답 형식 맞추기
//             JSONObject response = new JSONObject();
//             response.put("webSocketKey", UUID.randomUUID().toString());
//             return response.toString();
//         } catch (JSONException e) {
//             throw new RuntimeException("웹소켓 키 생성 중 오류가 발생했습니다.", e);
//         }
//     }

//     // 키 만료 여부 확인
//     private boolean isKeyExpired() {
//         return LocalDateTime.now().isAfter(this.keyExpirationTime);
//     }

//     // 한국투자증권 API를 통해 WebSocket 키를 가져오는 메서드
//     private String fetchWebSocketKey() {
//         String url = apiConfig.getRestapiUrl() + "/oauth2/Approval";

//         HttpHeaders headers = new HttpHeaders();
//         headers.set("appkey", apiConfig.getAppKey());
//         headers.set("appsecret", apiConfig.getAppSecret());

//         Map<String, String> body = new HashMap<>();
//         body.put("grant_type", "client_credentials");
//         body.put("appkey", apiConfig.getAppKey());
//         body.put("secretkey", apiConfig.getAppSecret());

//         HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

//         try {
//             ResponseEntity<Map> response =
//                     restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
//             Map<String, Object> responseBody = response.getBody();

//             if (responseBody != null && responseBody.containsKey("approval_key")) {
//                 return (String) responseBody.get("approval_key");
//             } else {
//                 throw new RuntimeException("WebSocket approval_key not found in response");
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//             throw new RuntimeException("Failed to fetch WebSocket key", e);
//         }
//     }
// }
