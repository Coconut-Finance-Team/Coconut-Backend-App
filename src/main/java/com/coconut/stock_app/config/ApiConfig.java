package com.coconut.stock_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {
    @Value("${koreainvestment.appKey}")
    private String appKey;

    @Value("${koreainvestment.appSecret}")
    private String appSecret;

    @Value("${koreainvestment.apiUrl}")
    private String apiUrl;

    public String getAppKey() {
        return appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getApiUrl() {
        return apiUrl;
    }
}
