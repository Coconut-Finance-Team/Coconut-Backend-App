package com.coconut.stock_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadConfig {
    @Bean
    public ThreadPoolTaskExecutor webSocketExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);  // 항상 유지할 스레드 수
        executor.setMaxPoolSize(1);   // 최대 스레드 수
        executor.setQueueCapacity(1); // 대기열 크기
        executor.setThreadNamePrefix("websocket-"); // 스레드 이름 접두사
        return executor;
    }
}