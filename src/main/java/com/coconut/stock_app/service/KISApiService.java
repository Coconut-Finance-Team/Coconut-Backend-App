package com.coconut.stock_app.service;

import com.coconut.stock_app.config.ApiConfig;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KISApiService {
    private final ApiConfig apiConfig;
    private final RestTemplate restTemplate;
    private String accessToken; // 토큰이 만료될 때마다 값 갱신
    private Instant tokenExpiryTime; // 토큰 만료 시간을 저장하는 변수

    public String getAccessToken() {
        // 토큰이 존재하고 만료되지 않은 경우 캐시된 토큰을 반환
        if (accessToken != null && Instant.now().isBefore(tokenExpiryTime)) {
            System.out.println("Using cached access token: " + accessToken);
            return accessToken;
        }

        // 새로운 토큰을 발급 요청
        String url = apiConfig.getRestapiUrl() + "/oauth2/tokenP";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("appkey", apiConfig.getAppKey());
        headers.set("appsecret", apiConfig.getAppSecret());

        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "client_credentials");
        body.put("appkey", apiConfig.getAppKey());
        body.put("appsecret", apiConfig.getAppSecret());

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("access_token")) {
                accessToken = (String) responseBody.get("access_token");
                tokenExpiryTime = Instant.now().plusSeconds(60); // 토큰 유효 시간 1분 설정
                System.out.println("New access token generated: " + accessToken);
                return accessToken;
            } else {
                throw new RuntimeException("Access token not found in the response");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get access token", e);
        }
    }

    public String getWebSocketKey(String apiUrl) { // URL을 메서드 매개변수로 받음
        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 바디 설정
        JSONObject requestBody = new JSONObject();
        requestBody.put("grant_type", "client_credentials");
        requestBody.put("appkey", apiConfig.getAppKey());
        requestBody.put("secretkey", apiConfig.getAppSecret());

        // HTTP 엔티티 생성 (헤더 + 바디)
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        // RestTemplate 사용하여 POST 요청 보내기
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl + "/oauth2/Approval", HttpMethod.POST, entity,
                String.class);

        // 응답 JSON에서 approval_key 추출
        JSONObject responseBody = new JSONObject(response.getBody());
        String approvalKey = responseBody.getString("approval_key");

        System.out.println("WebSocket 접속키: " + approvalKey);
        return approvalKey;
    }
}