package com.coconut.stock_app.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ApiConfig {
    @Value("${koreainvestment.appKey}")
    private String appKey;

    @Value("${koreainvestment.appSecret}")
    private String appSecret;

    @Value("${koreainvestment.apiUrl}")
    private String apiUrl;

}
