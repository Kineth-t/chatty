package com.chat_project.Chatty.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable simple broker for both /user and /topic destinations
        registry.enableSimpleBroker("/user", "/topic");
        
        // Prefix for @MessageMapping methods
        registry.setApplicationDestinationPrefixes("/app");
        
        // Prefix for user-specific destinations
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket endpoint with CORS support
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:5173") // Allow all origins for development
                .withSockJS();
    }
}