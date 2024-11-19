package com.coconut.stock_app.config;

import com.coconut.stock_app.websocket.StockWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.coconut.stock_app.websocket.IndexWebSocketHandler;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final IndexWebSocketHandler indexWebSocketHandler;
    private final StockWebSocketHandler stockWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(indexWebSocketHandler, "/ws/stock-index").setAllowedOrigins("*");
        registry.addHandler(stockWebSocketHandler, "/ws/stock/*").setAllowedOrigins("*");
    }
}
