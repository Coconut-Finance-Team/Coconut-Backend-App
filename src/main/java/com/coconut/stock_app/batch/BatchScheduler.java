package com.coconut.stock_app.batch;

import com.coconut.stock_app.websocket.KISWebSocketClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchScheduler {
    private final KISWebSocketClient kisWebSocketClient;

    // 5분마다 배치 작업 실행
    @Scheduled(fixedRate = 100000)
    public void runBatch() {
        System.out.println("배치 작업 시작...");
        kisWebSocketClient.saveStockDataToMySQL();
        System.out.println("배치 작업 완료.");
    }
}
