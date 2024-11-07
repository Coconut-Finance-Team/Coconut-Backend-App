package com.coconut.stock_app.service;

import com.coconut.stock_app.config.ApiConfig;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KoreaInvestmentTokenService {
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
        String url = apiConfig.getApiUrl() + "/oauth2/tokenP";

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
}