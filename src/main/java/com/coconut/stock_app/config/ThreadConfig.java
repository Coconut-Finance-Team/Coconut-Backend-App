package com.coconut.stock_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadConfig {

    @Bean
    public ThreadPoolTaskExecutor webSocketExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 항상 유지할 최소 스레드 수
        executor.setCorePoolSize(5);

        // 최대 스레드 수 (부하 시 확장 가능)
        executor.setMaxPoolSize(20);

        // 대기열 크기 (최대 큐 크기)
        executor.setQueueCapacity(100);

        // 대기 중인 스레드를 유지할 시간 (초 단위)
        executor.setKeepAliveSeconds(60);

        // 디버깅 및 관리 편의를 위한 스레드 이름 접두사
        executor.setThreadNamePrefix("websocket-");

        // 스레드 풀 초기화
        executor.initialize();

        return executor;
    }
}
