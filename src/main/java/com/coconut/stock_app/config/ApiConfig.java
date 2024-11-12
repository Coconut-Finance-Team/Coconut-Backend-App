package com.coconut.stock_app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "koreainvestment")
public class ApiConfig {
    private String appKey;
    private String appSecret;
    private String restapiUrl;
    private String stockPriceEndpoint;
}
