package com.coconut.stock_app.websocket;

import com.coconut.stock_app.config.ApiConfig;
import com.coconut.stock_app.dto.stock.StockChartDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * KIS API 키와 인증 토큰을 관리하는 클래스 - approval_key와 bearer_token 발급, 캐싱, 갱신 기능을 통합적으로 처리
 */
@Service
@RequiredArgsConstructor
public class KISApiService {
    private static final String APPROVAL_KEY_CACHE_KEY = "approval_key";
    private static final String BEARER_TOKEN_CACHE_KEY = "bearer_token";
    private static final String REDIS_KEY_PREFIX = "stock:";

    private final ApiConfig apiConfig;
    private final RestTemplate restTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String accessToken;
    private Instant tokenExpiryTime;

    /**
     * approval_key를 반환합니다.
     */
    public String getApprovalKey() {
        String cachedKey = redisTemplate.opsForValue().get(APPROVAL_KEY_CACHE_KEY);
        if (cachedKey != null) {
            System.out.println("Redis 캐싱된 approval_key 사용: " + cachedKey);
            return cachedKey;
        }

        String newKey = fetchApprovalKey();
        redisTemplate.opsForValue().set(APPROVAL_KEY_CACHE_KEY, newKey, 1, TimeUnit.MINUTES);
        System.out.println("새로운 approval_key 발급 및 Redis 저장: " + newKey);
        return newKey;
    }

    /**
     * bearer_token을 반환합니다.
     */
    public String getBearerToken() {
        String cachedToken = redisTemplate.opsForValue().get(BEARER_TOKEN_CACHE_KEY);
        if (cachedToken != null) {
            System.out.println("Redis 캐싱된 bearer_token 사용: " + cachedToken);
            return cachedToken;
        }

        String newToken = fetchBearerToken();
        redisTemplate.opsForValue().set(BEARER_TOKEN_CACHE_KEY, newToken, 1, TimeUnit.HOURS);
        System.out.println("새로운 bearer_token 발급 및 Redis 저장: " + newToken);
        return newToken;
    }

    /**
     * access_token을 반환합니다. 캐싱된 값이 유효하면 사용하고, 없거나 만료된 경우 새로 발급받아 갱신합니다.
     */
    public String getAccessToken() {
        if (accessToken != null && Instant.now().isBefore(tokenExpiryTime)) {
            System.out.println("캐싱된 access_token 사용: " + accessToken);
            return accessToken;
        }

        String url = apiConfig.getAccessTokenEndpoint();

        HttpHeaders headers = createHeaders();
        HttpEntity<Map<String, String>> request = createRequestEntity();

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            JSONObject responseBody = new JSONObject(response.getBody());
            String newAccessToken = responseBody.optString("access_token");

            if (newAccessToken.isEmpty()) {
                throw new RuntimeException("Access token 발급 실패: 응답에 access_token 없음");
            }

            accessToken = newAccessToken;
            tokenExpiryTime = Instant.now().plusSeconds(3600); // 토큰 유효 시간 1시간
            System.out.println("새로운 access_token 발급 성공: " + accessToken);
            return accessToken;
        } catch (Exception e) {
            System.err.println("access_token 발급 중 오류: " + e.getMessage());
            throw new RuntimeException("Access token 발급 요청 실패", e);
        }
    }

    /**
     * approval_key 발급 로직
     */
    private String fetchApprovalKey() {
        System.out.println("approval_key 발급 요청 중...");
        String url = apiConfig.getApprovalKeyEndpoint();

        HttpHeaders headers = createHeaders();
        HttpEntity<Map<String, String>> request = createRequestEntity();

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            JSONObject responseBody = new JSONObject(response.getBody());
            String approvalKey = responseBody.optString("approval_key");

            if (approvalKey.isEmpty()) {
                throw new RuntimeException("approval_key 발급 실패: 응답에 approval_key 없음");
            }

            System.out.println("approval_key 발급 성공: " + approvalKey);
            return approvalKey;
        } catch (Exception e) {
            System.err.println("approval_key 발급 중 오류: " + e.getMessage());
            throw new RuntimeException("approval_key 발급 요청 실패", e);
        }
    }

    /**
     * approval_key를 무효화합니다.
     */
    public void invalidateApprovalKey() {
        redisTemplate.delete(APPROVAL_KEY_CACHE_KEY);
        System.out.println("[KISApiService] approval_key 캐시 삭제 완료");
    }

    public void saveToRedis(StockChartDTO stockChartDTO) {
        try {
            // 객체를 JSON으로 변환하여 Redis에 저장
            String key = REDIS_KEY_PREFIX + stockChartDTO.getStockCode();
            String json = objectMapper.writeValueAsString(stockChartDTO);
            redisTemplate.opsForList().leftPush(key, json);

            System.out.println("Redis에 저장 완료: " + key);
        } catch (JsonProcessingException e) {
            System.err.println("Redis 직렬화 오류: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Redis 저장 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * bearer_token 발급 로직
     */
    private String fetchBearerToken() {
        System.out.println("bearer_token 발급 요청 중...");
        String url = apiConfig.getAccessTokenEndpoint();

        HttpHeaders headers = createHeaders();
        HttpEntity<Map<String, String>> request = createRequestEntity();

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            JSONObject responseBody = new JSONObject(response.getBody());
            String bearerToken = responseBody.optString("access_token");

            if (bearerToken.isEmpty()) {
                throw new RuntimeException("bearer_token 발급 실패: 응답에 access_token 없음");
            }

            System.out.println("bearer_token 발급 성공: " + bearerToken);
            return bearerToken;
        } catch (Exception e) {
            System.err.println("bearer_token 발급 중 오류: " + e.getMessage());
            throw new RuntimeException("bearer_token 발급 요청 실패", e);
        }
    }

    /**
     * 공통 헤더 생성
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("appkey", apiConfig.getAppKey());
        headers.set("appsecret", apiConfig.getAppSecret());
        return headers;
    }

    /**
     * 공통 요청 엔티티 생성
     */
    private HttpEntity<Map<String, String>> createRequestEntity() {
        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "client_credentials");
        body.put("appkey", apiConfig.getAppKey());
        body.put("secretkey", apiConfig.getAppSecret());
        return new HttpEntity<>(body, createHeaders());
    }
}
