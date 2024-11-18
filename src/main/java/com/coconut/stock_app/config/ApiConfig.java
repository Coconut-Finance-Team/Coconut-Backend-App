package com.coconut.stock_app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "koreainvestment")
public class ApiConfig {
    private String appKey;
    private String appSecret;
    private String restapiUrl;
    private String approvalKeyEndpoint;
    private String accessTokenEndpoint;
    private String stockPriceEndpoint;
    private String stockIndexEndpoint;
    private String websocketUrl;
    private int websocketRetryMaxAttempts;
    private int websocketRetryInitialDelay;
}
