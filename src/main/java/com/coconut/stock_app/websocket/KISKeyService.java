package com.coconut.stock_app.websocket;

import com.coconut.stock_app.config.ApiConfig;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class KISKeyService {

    private static final String APPROVAL_KEY_CACHE_KEY = "approval_key";
    private static final long CACHE_EXPIRATION_MINUTES = 1; // 캐싱 시간

    private final ApiConfig apiConfig;
    private final RestTemplate restTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * approval_key를 반환합니다.
     * 캐싱된 키가 있으면 반환하고, 없으면 새로 발급합니다.
     */
    public String getApprovalKey() {
        String cachedKey = redisTemplate.opsForValue().get(APPROVAL_KEY_CACHE_KEY);
        if (cachedKey != null) {
            logInfo("Redis 캐싱된 approval_key 사용: " + cachedKey);
            return cachedKey;
        }
        return fetchAndCacheApprovalKey();
    }

    /**
     * approval_key를 무효화하여 캐시를 삭제합니다.
     */
    public void invalidateApprovalKey() {
        redisTemplate.delete(APPROVAL_KEY_CACHE_KEY);
        logInfo("approval_key 캐시 삭제 완료");
    }

    /**
     * approval_key를 발급하고 Redis에 캐싱합니다.
     */
    private String fetchAndCacheApprovalKey() {
        logInfo("approval_key 발급 요청 중...");
        String url = apiConfig.getApprovalKeyEndpoint();

        try {
            HttpEntity<Map<String, String>> request = createRequestEntity();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            String approvalKey = extractApprovalKey(response);
            cacheApprovalKey(approvalKey);

            logInfo("approval_key 발급 성공 및 Redis 저장: " + approvalKey);
            return approvalKey;
        } catch (Exception e) {
            logError("approval_key 발급 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("approval_key 발급 요청 실패", e);
        }
    }

    /**
     * API 응답에서 approval_key를 추출합니다.
     */
    private String extractApprovalKey(ResponseEntity<String> response) {
        JSONObject responseBody = new JSONObject(response.getBody());
        String approvalKey = responseBody.optString("approval_key");
        if (approvalKey.isEmpty()) {
            throw new RuntimeException("approval_key 발급 실패: 응답에 approval_key 없음");
        }
        return approvalKey;
    }

    /**
     * Redis에 approval_key를 저장합니다.
     */
    private void cacheApprovalKey(String approvalKey) {
        redisTemplate.opsForValue().set(APPROVAL_KEY_CACHE_KEY, approvalKey, CACHE_EXPIRATION_MINUTES, TimeUnit.MINUTES);
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
     * 로깅: 정보 수준
     */
    private void logInfo(String message) {
        System.out.println("[KISApprovalKeyService] INFO: " + message);
    }

    /**
     * 로깅: 오류 수준
     */
    private void logError(String message) {
        System.err.println("[KISApprovalKeyService] ERROR: " + message);
    }
}
