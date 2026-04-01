package com.davidruffner.homecontrollerbackend.config;

import com.davidruffner.homecontrollerbackend.websockets.InventoryWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final InventoryWebSocketHandler inventoryWebSocketHandler;

    public WebSocketConfig(InventoryWebSocketHandler inventoryWebSocketHandler) {
        this.inventoryWebSocketHandler = inventoryWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(inventoryWebSocketHandler, "/ws")
            .setAllowedOriginPatterns("*");
    }
}