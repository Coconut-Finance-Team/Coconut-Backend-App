package com.coconut.stock_app.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WebSocketKeyServiceTest {

    @Autowired
    private WebSocketKeyService webSocketKeyService;

    @Test
    public void testGetWebSocketKey() {
        String result = webSocketKeyService.getWebSocketKey();

        // JSON 파싱 테스트
        try {
            JSONObject jsonObject = new JSONObject(result);
            assertNotNull(jsonObject.getString("webSocketKey"));
            assertTrue(jsonObject.getString("webSocketKey").length() > 0);
        } catch (JSONException e) {
            fail("올바른 JSON 형식이 아닙니다: " + result);
        }
    }
}
