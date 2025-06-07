package com.example.mcpserver.config;

import com.example.mcpserver.handler.McpWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    private final McpWebSocketHandler mcpWebSocketHandler;
    
    public WebSocketConfig(McpWebSocketHandler mcpWebSocketHandler) {
        this.mcpWebSocketHandler = mcpWebSocketHandler;
    }
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(mcpWebSocketHandler, "/mcp")
                .setAllowedOrigins("http://localhost:8081", "http://localhost:3000", "http://127.0.0.1:8081")
                .withSockJS();
    }
}